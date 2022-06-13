package com.hector.modulo6y7;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.RankingsClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.jos.games.ranking.ScoreSubmissionInfo;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainAcitivty";
    TextView textViewInfo;
    EditText editTextScore;
    HuaweiIdAuthButton buttonLogin;
    Button buttonIndentity, buttonGetPlayerInfo, buttonGetAchievementList, buttonReachAchievement, buttonLeaderboar, buttonScore;

    AchievementsClient achievementsClient;
    RankingsClient rankingsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewInfo = findViewById(R.id.textViewInfo);
        editTextScore = findViewById(R.id.editTextScore);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonIndentity = findViewById(R.id.buttonIndentity);
        buttonGetPlayerInfo = findViewById(R.id.buttonGetPlayerInfo);
        buttonGetAchievementList = findViewById(R.id.buttonGetAchievementList);
        buttonReachAchievement = findViewById(R.id.buttonReachAchievement);
        buttonLeaderboar = findViewById(R.id.buttonLeaderboar);
        buttonScore = findViewById(R.id.buttonScore);

        init();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginClick();
            }
        });

        buttonGetPlayerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGamePlayer();
            }
        });

        buttonGetAchievementList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAchievementIntent();
            }
        });

        buttonReachAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reachAchievement("C36E141F08E0A8FABB9516C17AEBB40434FD4559E991F9CE50C298ABD6E80517");
            }
        });

        buttonLeaderboar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leadearBoradOnClick();
            }
        });

        buttonScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String scoreString = editTextScore.getText().toString();
                    int score = Integer.parseInt(scoreString);

                    buttonScoreOnClick(score);
                }catch (Exception ex) {

                }
            }
        });
    }

    private void leadearBoradOnClick() {
        Task<Intent> intentTask = rankingsClient.getTotalRankingsIntent();
        intentTask.addOnSuccessListener(new OnSuccessListener<Intent>() {
            @Override
            public void onSuccess(Intent intent) {
                try {
                    // Call startActivityForResult to start a leaderboard selection page.
                    startActivityForResult(intent, 100);
                } catch (Exception e) {
                    // Handle the exception.
                }
            }
        });
        intentTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    // Send back a result code.
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                }
            }
        });
    }

    private void buttonScoreOnClick(int score) {
        Task<ScoreSubmissionInfo> scoreTask= rankingsClient.submitScoreWithResult("7C94FA2DEEE165A19F9F6257722803D1D765597F77378D6CB9552AEAA6165CEA", score);
        scoreTask.addOnSuccessListener(new OnSuccessListener<ScoreSubmissionInfo>() {
            @Override
            public void onSuccess(ScoreSubmissionInfo scoreTask) {
                // Submission success.
                textViewInfo.setText(scoreTask.toString());
            }
        });
        scoreTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    // Send back a result code.
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                }
            }
        });
    }

    private void init() {
        JosAppsClient appsClient = JosApps.getJosAppsClient(this);
        appsClient.init();
        Log.i(TAG, "init success");

        achievementsClient = Games.getAchievementsClient(this);
        rankingsClient = Games.getRankingsClient(this);
    }

    private void onLoginClick(){
        AccountAuthParams authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
        AccountAuthService service = AccountAuthManager.getService(MainActivity.this, authParams);
        startActivityForResult(service.getSignInIntent(), 8888);
    }

    private void silentSignInOnClick(){

    }

    public void getGamePlayer() {
        // Call the getPlayersClient method for initialization.
        PlayersClient client = Games.getPlayersClient(this);
        // Obtain player information.
        Task<Player> task = client.getGamePlayer();
        task.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                String accessToken = player.getAccessToken();
                String displayName = player.getDisplayName();
                String unionId = player.getUnionId();
                String openId = player.getOpenId();
                textViewInfo.setText(displayName );
                // The player information is successfully obtained. Your game is started after accessToken is verified.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    // Failed to obtain player information. Rectify the fault based on the result code.
                    textViewInfo.setText(result);
                }
            }
        });
    }

    private void getAchievementIntent(){
        Task<Intent> task = achievementsClient.getShowAchievementListIntent();
        task.addOnSuccessListener(new OnSuccessListener<Intent>() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent == null) {
                    Log.w("Achievement", "intent = null");
                } else {
                    try {
                        startActivityForResult(intent, 1);
                    } catch (Exception e) {
                        Log.e("Achievement", "Achievement Activity is Invalid");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    int rtnCode = ((ApiException) e).getStatusCode();
                    String result = "rtnCode:" + rtnCode;
                }
            }
        });

    }

    private void reachAchievement(String achievementId){
        Task<Void> task = achievementsClient.reachWithResult(achievementId);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                Log.i("Achievement","reach  success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:"
                            + ((ApiException) e).getStatusCode();
                    Log.e("Achievement","reach result" + result);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result to obtain the authorization code from AuthAccount.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the user's ID information and authorization code are obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i(TAG, "serverAuthCode:" + authAccount.getAuthorizationCode());
            } else {
                // The sign-in failed.
                Log.e(TAG, "sign in failed:" + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }
}