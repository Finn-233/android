package com.example.mybroadcast.LoginFragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mybroadcast.MainActivity;
import com.example.mybroadcast.R;
import com.example.mybroadcast.dbHelper.SQLInformation;
import com.example.mybroadcast.dbHelper.dbHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private EditText username, password;
    private Button button;
    private CheckBox checkBox;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        view.findViewById(R.id.adduser).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registeredFragment));
        username = view.findViewById(R.id.usern);
        password = view.findViewById(R.id.passw);
        SharedPreferences settings = getContext().getSharedPreferences("setting", MODE_PRIVATE);//打开Preferences系统自动判断，如果存在就打开，否则创建。
        username.setText(settings.getString("username", ""));
        password.setText(settings.getString("password", ""));
        button = view.findViewById(R.id.loginbut);
        checkBox = view.findViewById(R.id.rember);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper helper = new dbHelper(getActivity(), SQLInformation.Name, null, SQLInformation.version);
                SQLiteDatabase db = helper.getReadableDatabase();
                String sql = "select * from tb_user where username=? and password=?";
                Cursor cursor = db.rawQuery(sql, new String[]{username.getText().toString().trim(), password.getText().toString().trim()});

                if (cursor.moveToNext()) {
//                    BMainActivity.loginuser = new QQContactitem(
//                            cursor.getString(cursor.getColumnIndex("qq_name")),
//                            cursor.getString(cursor.getColumnIndex("qq_online")),
//                            cursor.getString(cursor.getColumnIndex("qq_action")),
//                            cursor.getString(cursor.getColumnIndex("qq_num")),
//                            cursor.getString(cursor.getColumnIndex("belong_country")),
//                            cursor.getString(cursor.getColumnIndex("qq_imgurl")));
                    if (checkBox.isChecked()) {
                        SharedPreferences settings = getContext().getSharedPreferences("setting", MODE_PRIVATE);//打开Preferences系统自动判断，如果存在就打开，否则创建。
                        SharedPreferences.Editor editor = settings.edit();//使其处于编辑状态
                        editor.putString("username", username.getText().toString());
                        editor.putString("password", password.getText().toString());//存放数据
                        editor.commit();//提交数据
                    }
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getActivity(), "密码或账号错误", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
