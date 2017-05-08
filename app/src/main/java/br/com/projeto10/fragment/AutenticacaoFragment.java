package br.com.projeto10.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import br.com.projeto10.activity.MainActivity;
import br.com.projeto10.activity.R;
import br.com.projeto10.modelo.Usuario;


public class AutenticacaoFragment extends Fragment implements Validator.ValidationListener{


    @NotEmpty(messageResId = R.string.msg_erro_campo_obrigatorio)
    private EditText edLogin,edSenha;


    private Validator validator;

    private Button btnEntrar;

    public AutenticacaoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_autenticacao, container, false);


        edLogin = (EditText) view.findViewById(R.id.edLogin);
        edSenha = (EditText) view.findViewById(R.id.edSenha);

        btnEntrar = (Button) view.findViewById(R.id.btnEntrar);

        validator = new Validator(this);
        validator.setValidationListener(this);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });


        return view;
    }


    @Override
    public void onValidationSucceeded() {


        Usuario usuario = new Usuario();
        usuario.setIdUsuario(101);
        usuario.setNome("Diego Muguet");
        usuario.setLogin(edLogin.getText().toString());

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("PARAM_USUARIO",usuario);
        startActivity(intent);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {


        for(ValidationError error:errors){
            EditText editText = (EditText) error.getView();
            String mensagem = error.getCollatedErrorMessage(getContext());
            editText.setError(mensagem);
        }
    }
}
