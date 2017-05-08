package br.com.projeto10.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.ByteArrayOutputStream;

import br.com.projeto10.fragment.AutenticacaoFragment;
import br.com.projeto10.fragment.ContatosFragment;
import br.com.projeto10.fragment.ListaContatosFragment;
import br.com.projeto10.modelo.Usuario;

public class MainActivity extends AppCompatActivity {

    public static final String PARAM_USUARIO = "PARAM_USUARIO";
    public static final String TAG_FRAG_LISTA_CONTATOS = "TAG_FRAG_LISTA_CONTATOS";
    public static final String TAG_FRAG_NOVO_CONTATOS = "TAG_FRAG_NOVO_CONTATOS";
    private static final int REQ_CODE = 123;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private AccountHeader accountHeader;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = (Usuario) getIntent().getSerializableExtra(PARAM_USUARIO);

        fragmentManager = getSupportFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.toolBarTop);
        toolbar.setTitle(getString(R.string.str_contatos));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        PrimaryDrawerItem itemListaContatos = new PrimaryDrawerItem().withName(R.string.str_lista_contatos)
                .withIcon(getDrawable(R.drawable.ic_contato))
                .withSelectedIcon(getDrawable(R.drawable.ic_contato_selected));

        PrimaryDrawerItem itemNovoContatos = new PrimaryDrawerItem().withName(R.string.str_novo_contato)
                .withIcon(getDrawable(R.drawable.ic_novo_contato))
                .withSelectedIcon(getDrawable(R.drawable.ic_novo_contato_selected));

        SecondaryDrawerItem itemSair = new SecondaryDrawerItem().withName(getString(R.string.str_sair))
                .withIcon(getDrawable(R.drawable.ic_sair)).withSelectedIcon(getDrawable(R.drawable.ic_sair_selected));

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(usuario.getNome())
                .withEmail(usuario.getLogin());

        if(usuario.getBlobFoto() != null && usuario.getBlobFoto().length > 0){
            profileDrawerItem.withIcon(BitmapFactory.decodeByteArray(usuario.getBlobFoto(),0,usuario.getBlobFoto().length));
        }else{
            profileDrawerItem.withIcon(getDrawable(R.drawable.profile));
        }

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this).withHeaderBackground(R.drawable.contatos)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(true)
                .addProfiles(profileDrawerItem)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

                        return false;
                    }
                }).withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        try {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQ_CODE);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
        .build();

        Drawer menu = new DrawerBuilder()
                .withToolbar(toolbar)
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggleAnimated(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        Fragment fragment;

                        switch (position){
                            case 1:
//                                fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAG_LISTA_CONTATOS);

//                                if(fragment == null){
                                    // new
                                    fragment = new ListaContatosFragment();
//                                }

                                fragmentManager.beginTransaction().replace(R.id.container,fragment, TAG_FRAG_LISTA_CONTATOS)
                                .commit();


                                break;

                            case 2:

//                                fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAG_NOVO_CONTATOS);

//                                if(fragment == null){
                                    // new
                                    fragment = new ContatosFragment();
//                                }

                                fragmentManager.beginTransaction().replace(R.id.container,fragment, TAG_FRAG_NOVO_CONTATOS)
                                        .commit();

//                                Intent intent = new Intent(MainActivity.this, ContatoActivity.class);
//                                startActivity(intent);

                                break;

                            case 3:

                                break;

                            case 4:

                                break;
                        }

                        return false;
                    }
                }).build();

        menu.addItem(itemListaContatos);
        menu.addItem(itemNovoContatos);
        menu.addItem(new DividerDrawerItem());
        menu.addItem(itemSair);

        menu.setSelectionAtPosition(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE && resultCode == RESULT_OK){


            Bitmap imagem = (Bitmap)data.getExtras().get("data");

            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG,100,streamOut);
            byte[] byteArray = streamOut.toByteArray();
            usuario.setBlobFoto(byteArray);

            atualizarProfile();
        }
    }

    private void atualizarProfile() {
        if(usuario.getBlobFoto() != null){
            Bitmap imagem = BitmapFactory.decodeByteArray(usuario.getBlobFoto(),0,usuario.getBlobFoto().length);
            ProfileDrawerItem profile = (ProfileDrawerItem)accountHeader.getProfiles().get(0);

            profile.withIcon(imagem);

            accountHeader.removeProfile(0);
            accountHeader.addProfile(profile,0);

        }

    }
}
