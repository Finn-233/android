package com.example.mybroadcast.LoginFragment;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mybroadcast.R;
import com.example.mybroadcast.dbHelper.SQLInformation;
import com.example.mybroadcast.dbHelper.dbHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisteredFragment extends Fragment {
    private EditText username, email, telphone, password;
    boolean flag = true;

    public RegisteredFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registered, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View view = getView();
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        telphone = view.findViewById(R.id.telphone);
        password = view.findViewById(R.id.password);

        view.findViewById(R.id.zhuce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper helper = new dbHelper(getActivity(), SQLInformation.Name, null, SQLInformation.version);
                if (username.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "用户名为空", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "邮箱为空", Toast.LENGTH_SHORT).show();
                } else if (telphone.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "手机为空", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "密码为空", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username.getText().toString().trim());
                    bundle.putString("email", email.getText().toString().trim());
                    bundle.putString("telphone", telphone.getText().toString().trim());
                    bundle.putString("password", password.getText().toString().trim());


                    SQLiteDatabase db = helper.getWritableDatabase();
                    String sql = "insert into tb_user(username,password,email,telphone)values(?,?,?,?)";
                    db.execSQL(sql, new String[]{username.getText().toString().trim(), password.getText().toString().trim(), email.getText().toString().trim(), telphone.getText().toString().trim()});


                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_registeredFragment_to_loginFragment, bundle);
                    Toast.makeText(getActivity(), "注册成功！", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}