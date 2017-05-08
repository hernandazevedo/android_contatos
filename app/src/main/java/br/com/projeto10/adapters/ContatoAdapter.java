package br.com.projeto10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.projeto10.activity.R;
import br.com.projeto10.modelo.Contato;

/**
 * Created by atc1n on 02/03/2017.
 */

public class ContatoAdapter extends BaseAdapter {

    private Context context;
    private List<Contato> lista;

    public ContatoAdapter(Context context,List<Contato> lista){
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getIdContato();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {


        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.contato_item, null);
            view.setTag(view);
        } else {
            view = (View) view.getTag();
        }

        Contato currentListData = (Contato)getItem(position);

        ((TextView)view.findViewById(R.id.tvNomeItem)).setText(currentListData.getNome());
        ((TextView)view.findViewById(R.id.tvTelefoneItem)).setText(currentListData.getTelefone());

        if(currentListData.getOperadora() != null)
            ((TextView)view.findViewById(R.id.tvOperadoraItem)).setText(currentListData.getOperadora().getNome());


        return view;
    }
}
