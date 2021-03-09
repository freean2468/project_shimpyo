package com.mirae.shimpyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    private Button login_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KakaoSdk.init(this,"kakaof31b2fa28fb649476be3e0f6db9024f5");

        login_Button = findViewById(R.id.login_Button);
        SuccessScene scene = new SuccessScene();
        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginClient.getInstance().loginWithKakaoAccount(getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {
                    @Override
                    public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                        if(throwable!=null)
                        {
                            Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                        else if(oAuthToken!=null)
                        {
                            Toast.makeText(getApplicationContext(),"로그인 성",Toast.LENGTH_SHORT).show();
                        }
                        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                            @Override
                            public Unit invoke(User user, Throwable throwable) {
                                if(throwable!=null)
                                {
                                    Toast.makeText(getApplicationContext(),"사용자 정보 받기 실패",Toast.LENGTH_SHORT).show();
                                }
                                else if(user!=null)
                                {
                                    Toast.makeText(getApplicationContext(),"사용자 정보 받기 실패",Toast.LENGTH_SHORT).show();
                                    String nickName = user.getKakaoAccount().getProfile().getNickname();
                                    Intent intent = new Intent(getApplicationContext(), scene.getClass());
                                    startActivity(intent);
                                }

                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

    }
}