package br.com.projeto10.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Select;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.projeto10.activity.ContatoActivity;
import br.com.projeto10.activity.R;
import br.com.projeto10.modelo.Contato;
import br.com.projeto10.modelo.Operadora;
import br.com.projeto10.util.Util;


public class ContatosFragment extends Fragment implements Validator.ValidationListener {

    private Button btnSalvar;

    @NotEmpty(messageResId = R.string.msg_erro_campo_obrigatorio)
    private EditText edNome,edTelefone,edNascimento;

    private Exception exThread;

    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Contato contato;


    private Spinner spOperadora;
    private Validator validator;
    private List<Operadora> listaOperadora;

    public ContatosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        toolbar = (Toolbar)view.findViewById(R.id.toolBarTopContatos);
        toolbar.setTitle(getString(R.string.str_contatos));

        if(Util.isExisteConexao(getContext())){
            new ContatosFragment.ThreadListaOperadoras().execute();
        }else{
            Toast.makeText(getContext(),getString(R.string.msg_erro_internet),Toast.LENGTH_LONG).show();
        }

        Intent intent = getActivity().getIntent();

        edNome = (EditText) view.findViewById(R.id.edNome);
        edTelefone = (EditText) view.findViewById(R.id.edTelefone);
        edNascimento = (EditText) view.findViewById(R.id.edNascimento);
        btnSalvar = (Button) view.findViewById(R.id.btnSalvar);

        validator = new Validator(this);
        validator.setValidationListener(this);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

//        edNascimento.addTextChangedListener(Mask.insert("##/##/####", edNascimento));

        edNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    onDateFocus();
                }
            }
        });


        edNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateFocus();
            }
        });
        spOperadora = (Spinner) view.findViewById(R.id.spOperadora);

        if(intent.hasExtra("contato")){
            contato = (Contato)intent.getSerializableExtra("contato");

            edNome .setText(contato.getNome());
            edTelefone.setText(contato.getTelefone());
            edNascimento.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(contato.getData())));
            ((ContatoActivity)getActivity()).setSupportActionBar(toolbar);
            ((ContatoActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

        }else{
            toolbar.setVisibility(View.GONE);
        }

        return view;
    }

    private void onDateFocus() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int diff = calendar.get(Calendar.YEAR) - year;
                if(diff < 15){
                    Toast.makeText(getContext(),"Data invalida",Toast.LENGTH_LONG).show();
                }else{
                    String strMes = "";
                    String strDia = "";
                    month = month + 1;
                    if(month <= 9){
                        strMes = "0" + String.valueOf(month);
                    }else{
                        strMes = String.valueOf(month);
                    }

                    if(dayOfMonth <= 9){
                        strDia = "0" + String.valueOf(dayOfMonth);
                    }else{
                        strDia = String.valueOf(dayOfMonth);
                    }

                    edNascimento.setText(strDia + "/" + strMes + "/" + String.valueOf(year));
                }
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void onValidationSucceeded() {
        try {
            if(contato == null) contato = new Contato();

            contato.setNome(edNome.getText().toString());
            contato.setTelefone(edTelefone.getText().toString());
            contato.setData(new SimpleDateFormat("dd/MM/yyyy").parse(edNascimento.getText().toString()).getTime());
            contato.setOperadora(listaOperadora.get(spOperadora.getSelectedItemPosition()));
            new ContatosFragment.ThreadCadastroContatos().execute();
        }catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for(ValidationError error:errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
            else if (view instanceof Spinner) {
                ((TextView) ((Spinner) view).getSelectedView()).setError(message);
            }
        }
    }

    public class ThreadListaOperadoras extends AsyncTask<Void,Integer,Boolean> {


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

                String json =  Util.realizarRequisicaoHttp("http://hernand.servehttp.com:8080/api/v1/operadoras/","GET",null);
                listaOperadora = Util.parseToOperadora(json);
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
                if(listaOperadora != null && !listaOperadora.isEmpty()){
                    spOperadora.setAdapter(new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,listaOperadora));

                    if(contato != null)
                        spOperadora.setSelection(listaOperadora.indexOf(contato.getOperadora()));
//                    spOperadora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Intent intent = new Intent(getContext(), ContatoActivity.class);
//                            intent.putExtra("contato",listaContato.get(position));
//                            startActivity(intent);
//                        }
//                    });
                }else{
                    Toast.makeText(getContext(),getString(R.string.msg_nenhum_reg),Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getContext(),exThread.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }

    public class ThreadCadastroContatos extends AsyncTask<Void,Integer,Boolean>{




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

                String url = "http://hernand.servehttp.com:8080/api/v1/contatos";
                String ttt = "http://hernand.servehttp.com:8080/api/v1/contatos/";
                String json =  Util.realizarRequisicaoHttp(url,"POST",Util.parseToContatoJSON(contato));

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

                if(contato != null && contato.getIdContato() == null) {
                    Toast.makeText(getContext(), "Contato salvo com sucesso.", Toast.LENGTH_LONG).show();
                    edNascimento.setText("");
                    edTelefone.setText("");
                    edNome.setText("");
                    spOperadora.setSelection(0);
                    contato = null;
                }else{
                    Toast.makeText(getContext(), "Contato Atualizado com sucesso.", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getContext(),exThread.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }

}
