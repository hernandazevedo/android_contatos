package br.com.projeto10.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import br.com.projeto10.activity.ContatoActivity;
import br.com.projeto10.activity.R;
import br.com.projeto10.adapters.ContatoAdapter;
import br.com.projeto10.modelo.Contato;
import br.com.projeto10.modelo.Operadora;
import br.com.projeto10.util.Util;

public class ListaContatosFragment extends Fragment {

    private Spinner spOperadora;
    private ListView lvContato;
    private ProgressDialog progressDialog;
    private Exception exThread;
    private List<Contato> listaContato;
    private List<Operadora> listaOperadora;

    public ListaContatosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_contatos, container, false);

        lvContato = (ListView)view.findViewById(R.id.lvContato);

        if(Util.isExisteConexao(getContext())){
            new ThreadListaContatos().execute();
        }else{
            Toast.makeText(getContext(),getString(R.string.msg_erro_internet),Toast.LENGTH_LONG).show();
        }



        return view;
    }


    public class ThreadListaContatos extends AsyncTask<Void,Integer,Boolean>{


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(getString(R.string.str_aguarde));
            progressDialog.setMessage(getString(R.string.msg_aguarde));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean retorno = true;

            try{

               String json =  Util.realizarRequisicaoHttp("http://hernand.servehttp.com:8080/api/v1/contatos/","GET",null);
               listaContato = Util.parseToContato(json);
            }catch (Exception e){
                exThread = e;
                retorno = false;
            }

            return retorno;
        }

        @Override
        protected void onPostExecute(Boolean retorno) {

            if(progressDialog != null){
                progressDialog.dismiss();
            }

            if(retorno){
                if(listaContato != null && !listaContato.isEmpty()){
                    lvContato.setAdapter(new ContatoAdapter(getContext(),listaContato));
                    lvContato.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(), ContatoActivity.class);
                            intent.putExtra("contato",listaContato.get(position));
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(getContext(),getString(R.string.msg_nenhum_reg),Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getContext(),exThread.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }


}
