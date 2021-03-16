package com.mirae.shimpyo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.mirae.shimpyo.R;
import com.mirae.shimpyo.fragment.FragmentDialogForLogin;
import com.mirae.shimpyo.object.ObjectVolley;

import static com.kakao.util.helper.Utility.getKeyHash;

/**
 * 앱의 첫 화면이자, 로그인 화면. 카카오 SDK를 이용해 카카오 회원번호를 웹서버에 전달, 자체 회원 정보를 가져온다.
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ActivityLogin extends ActivityNoSystemBar {
    /**
     * 로그인 액티비티에서만 카카오 세션을 유지한다.
     */
    private ISessionCallback sessionCallback = new ISessionCallback() {

        /** 로그인에 성공한 상태 */
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        /** 로그인에 실패한 상태 */
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        /** 사용자 정보 요청 */
        public void requestMe() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                }

                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Log.i("KAKAO_API", "사용자 아이디: " + result.getId());

                    /**
                     * 성공했으니 회원번호를 가지고 다음 Activity인 ActivityQA로 이동
                     *
                     * @author 송훈일(freean2468@gmail.com)
                     */
                    FragmentDialogForLogin fragmentDialogForLogin = new FragmentDialogForLogin(String.valueOf(result.getId()));
                    fragmentDialogForLogin.show(getSupportFragmentManager(), "login");

//                    UserAccount kakaoAccount = result.getKakaoAccount();
//                    if (kakaoAccount != null) {
//
//                        // 이메일
//                        String email = kakaoAccount.getEmail();
//
//                        if (email != null) {
//                            Log.i("KAKAO_API", "email: " + email);
//
//                        } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
//                            // 동의 요청 후 이메일 획득 가능
//                            // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
//
//                        } else {
//                            // 이메일 획득 불가
//                        }
//
//                        // 프로필
//                        Profile profile = kakaoAccount.getProfile();
//
//                        if (profile != null) {
//                            Log.d("KAKAO_API", "nickname: " + profile.getNickname());
//                            Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
//                            Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());
//
//                        } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
//                            // 동의 요청 후 프로필 정보 획득 가능
//
//                        } else {
//                            // 프로필 획득 불가
//                        }
//                    }
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        com.kakao.usermgmt.LoginButton buttonLogin = findViewById(R.id.buttonLogin);
        TextView textViewHostName = findViewById(R.id.textViewHostName);
        ObjectVolley objectVolley = ObjectVolley.getInstance(this);
        textViewHostName.setText(objectVolley.getHostName());

        Switch switchToggleServer = findViewById(R.id.switchToggleServer);
        switchToggleServer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            objectVolley.toggleUseCase();
            textViewHostName.setText(objectVolley.getHostName());
        });

        Session session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        buttonLogin.setOnClickListener(v -> {
            session.open(AuthType.KAKAO_LOGIN_ALL, ActivityLogin.this);
        });

        /**
         * 카카오 인증에 필요한 앱 해시값 가져오기
         */
        Log.d("MyHashKey", getKeyHash(getApplicationContext()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * 세션 콜백 삭제
         */
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /**
         * 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
         */
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
