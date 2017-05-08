package br.com.projeto10.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import br.com.projeto10.modelo.Contato;
import br.com.projeto10.modelo.Operadora;

/**
 * Created by atc1n on 02/03/2017.
 */

public class Util {

    public static String realizarRequisicaoHttp(String strURL, String metodo, JSONObject jsonObject) throws Exception{

        String jsonRetorno = null;
        URL url = new URL(strURL);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(metodo);
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setReadTimeout(10000);



        if(!metodo.equalsIgnoreCase("get") && jsonObject != null){
//            httpURLConnection.setUseCaches (false);

            httpURLConnection.setRequestProperty("Content-Type","application/json");
            httpURLConnection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(jsonObject.toString());

            dataOutputStream.flush();
            dataOutputStream.close();

            if(httpURLConnection.getResponseCode() != 201){
                throw new Exception("Erro interno na operacao");
            }
        }

        if(!metodo.equalsIgnoreCase("post")){
            httpURLConnection.setDoInput(true);
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            jsonRetorno = convertStreamToString(in);
            in.close();
        }




        return  jsonRetorno;
    }

    public static List<Operadora> parseToOperadora(String json) throws  Exception{

        List<Operadora> lista = new ArrayList<Operadora>();


        try{
            if(json != null && !json.isEmpty()) {
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    Operadora operadora = new Operadora();
                    JSONObject operadoraJSON = (JSONObject) jsonArray.get(i);

                    operadora = getOperadora(operadoraJSON);

                    lista.add(operadora);
                }

                return lista;
            }else{
                Collections.emptyList();
            }

        }catch (JSONException e){
            throw  new Exception(e.getMessage());
        }
        return Collections.emptyList();
    }

    @NonNull
    private static Operadora getOperadora(JSONObject operadoraJSON) throws JSONException {


        Operadora operadora = new Operadora();

        String nomeOperadora = operadoraJSON.getString("nome");
        Long idOperadora = operadoraJSON.getLong("id");
        String preco = operadoraJSON.getString("preco");

        operadora.setIdOperadora(idOperadora);
        operadora.setNome(nomeOperadora);

        if(preco != null && !preco.toString().contains("null") )
            operadora.setPreco(Double.parseDouble(preco));
        return operadora;
    }


    public static List<Contato> parseToContato(String json) throws  Exception{

        List<Contato> lista = new ArrayList<Contato>();


        try{
            if(json != null && !json.isEmpty()) {
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    Contato contato = new Contato();
                    JSONObject contatoJSON = (JSONObject) jsonArray.get(i);
                    Long id = contatoJSON.getLong("id");
                    Long data = contatoJSON.getLong("data");
                    String nome = contatoJSON.getString("nome");
                    String telefone = contatoJSON.getString("telefone");
                    String serial = contatoJSON.getString("serial");

                    JSONObject operadoraJSon = null;

                    if(contatoJSON.has("operadora") && !contatoJSON.isNull("operadora"))
                            operadoraJSon = contatoJSON.getJSONObject("operadora");

                    Operadora operadora = null;
                    if(operadoraJSon != null)
                      operadora = getOperadora(contatoJSON.getJSONObject("operadora"));


                    contato.setIdContato(id);
                    contato.setNome(nome);
                    contato.setData(data);
                    contato.setTelefone(telefone);
                    contato.setSerial(serial);
                    contato.setOperadora(operadora);

                    lista.add(contato);
                }

                return lista;
            }else{
                Collections.emptyList();
            }

        }catch (JSONException e){
            throw  new Exception(e.getMessage());
        }
        return Collections.emptyList();
    }

    public static boolean isExisteConexao(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String convertStreamToString(InputStream is) {

        //Buffer de leitura
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        //StringBuilder para armazenar o conteúdo
        StringBuilder sb = new StringBuilder();

        //Linha lida
        String line = null;

        try {

            //Enquanto houver dado a ser lido (ou seja, diferente de null)
            while ((line = reader.readLine()) != null) {

                //Armazena a string lida no StringBuilder (sb)
                sb.append(line + "\n");
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            try {
                is.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static JSONObject parseToContatoJSON(Contato contato) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id",contato.getIdContato());
        json.put("data",contato.getData());
        json.put("nome",contato.getNome());
        json.put("serial",null);
        json.put("telefone",contato.getTelefone());

        if(contato.getOperadora() != null)
            json.put("operadora",Util.parseToOperadoraJSON(contato.getOperadora()));


        return json;
    }

    public static JSONObject parseToOperadoraJSON(Operadora operadora)throws JSONException  {

        JSONObject json = new JSONObject();

        json.put("id",operadora.getIdOperadora());
        json.put("nome",operadora.getNome());
        json.put("preco",operadora.getPreco());

        return  json;
    }
}
