package za.nmu.wrpv.sos;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

 /**
 * SOS is a game played by 2 players in a 5 x 5 grid
 *  The object is to complete S-O-S patterns either horizontally, vertically or diagonally
 *  The player who completes most of these patterns wins
 *
 * @author Onke Sipuka
 * @version 1.0
 * @since 2020-10-07
 */
public class SosGameActivity extends AppCompatActivity implements OnClickListener{

    private boolean sButtonSelected; // Button S Selected
    private boolean isPlayerOne;
    private Button btnS;
    private Button btnO;
    private Button[] buttons = new Button[25];
    private int playerOneScore, playerTwoScore;
    private TextView txtPlayerOneScore, txtPlayerTwoScore;
    private TextView txtPlayerOne, txtPlayerTwo;
    private TextView winningPlayer;
    int[] gameState;
    int[][] sosPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_game);

        iniData();

    }
    /**
     *Method initialises the game state
    * */
    private void iniData(){

        /*
        *Keeps track of button state
        * S -> 0
        * O -> 1
        * empty -> 2
        */
        gameState = new int[]{
                2,2,2,2,2,
                2,2,2,2,2,
                2,2,2,2,2,
                2,2,2,2,2,
                2,2,2,2,2};

        //These are all the possible SOS positions in a 5 x 5 grid
        sosPositions = new int[][]{
                //Horizontal SOS
                {0, 1, 2}, {1, 2, 3}, {2, 3, 4},
                {5, 6, 7}, {6, 7, 8}, {7, 8, 9},
                {10, 11, 12}, {11, 12, 13}, {12, 13, 14},
                {15, 16, 17}, {16, 17, 18}, {17, 18, 19},
                {20, 21, 22}, {21, 22, 23}, {22, 23, 24},

                //Vertical SOS
                {0, 5, 10}, {5, 10, 15}, {10, 15, 20},
                {1, 6, 11}, {6, 11, 16}, {11, 16, 21},
                {2, 7, 12}, {7, 12, 17}, {12, 17, 22},
                {3, 8, 13}, {8, 13, 18}, {13, 18, 23},
                {4, 9, 14}, {9, 14, 19}, {14, 19, 24},

                //Positive Gradient Diagonal
                {2, 6, 10},
                {15, 11, 7}, {11, 7, 3},
                {20, 16, 12}, {16, 12, 8}, {12, 8, 4},
                {21, 17, 13}, {17, 13, 9},
                {22, 18, 14},

                //Negative Gradient Diagonal
                {10, 16, 22},
                {5, 11, 17}, {11, 17, 23},
                {0, 6, 12}, {6, 12, 18}, {12, 18, 24},
                {1, 7, 13}, {7, 13, 19},
                {2, 8, 14}

        };


        //Get S and O buttons
        btnO = findViewById(R.id.btnO);
        btnS = findViewById(R.id.btnS);

        //initialize buttons
        for(int i = 0; i < 25; i++){
            String buttonID = "btn" + i;
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resourceID);
            buttons[i].setOnClickListener(this);

        }

        //initialize scores
        playerOneScore = 0;
        playerTwoScore = 0;
        txtPlayerOneScore = findViewById(R.id.txtScorePlayerOne);
        txtPlayerTwoScore = findViewById(R.id.txtScorePlayerTwo);
        txtPlayerOneScore.setText(String.valueOf(playerOneScore));
        txtPlayerTwoScore.setText(String.valueOf(playerTwoScore));


        txtPlayerOne = findViewById(R.id.txtPlayerOne);
        txtPlayerTwo = findViewById(R.id.txtPlayerTwo);

        winningPlayer = findViewById(R.id.txtWinner);
        winningPlayer.setText("");

        isPlayerOne = true; // player one starts
        sButtonSelected = true; //starts with S selected

        btnS.setBackgroundColor(Color.parseColor("#2AF7AD"));
        txtPlayerOne.setBackgroundColor(Color.parseColor("#2AF7AD"));
        txtPlayerOneScore.setBackgroundColor(Color.parseColor("#2AF7AD"));

        txtPlayerTwo.setBackgroundColor(Color.parseColor("#ffffff"));
        txtPlayerTwoScore.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    @Override
    public void onClick(View v){

        if(!((Button) v).getText().toString().equals(""))
            return;

        String buttonID = v.getResources().getResourceEntryName(v.getId());
        int pointer = Integer.parseInt(buttonID.substring(3));

        if(sButtonSelected){
            ((Button)v).setText("S");
            gameState[pointer] = 0;
        }
        else{
            ((Button)v).setText("O");
            gameState[pointer] = 1;
        }

        ((Button)v).setTextColor(Color.parseColor("#ffffff"));

        v.setClickable(false);
        v.setBackgroundResource(R.drawable.my_selected_button_bg);
        isSOS(0);
        checkGameOver();
        isPlayerOne = !isPlayerOne;

        if(isPlayerOne){
            txtPlayerOne.setBackgroundColor(Color.parseColor("#2AF7AD"));
            txtPlayerOneScore.setBackgroundColor(Color.parseColor("#2AF7AD"));

            txtPlayerTwo.setBackgroundColor(Color.parseColor("#ffffff"));
            txtPlayerTwoScore.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else{
            txtPlayerTwo.setBackgroundColor(Color.parseColor("#2AF7AD"));
            txtPlayerTwoScore.setBackgroundColor(Color.parseColor("#2AF7AD"));

            txtPlayerOne.setBackgroundColor(Color.parseColor("#ffffff"));
            txtPlayerOneScore.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }


    /**
    * Method highlights the O button when clicked
    * */
    public void onButtonOClicked(View view) {
        sButtonSelected = false;
        btnO.setBackgroundColor(Color.parseColor("#2AF7AD"));
        btnS.setBackgroundResource(R.drawable.my_button_bg);
    }

    /**
      * Method highlights the S button when clicked
      * */
    public void onButtonSClicked(View view) {
        sButtonSelected = true;
        btnS.setBackgroundColor(Color.parseColor("#2AF7AD"));
        btnO.setBackgroundResource(R.drawable.my_button_bg);
    }

    /**
     * Method check if S-O-S has been formed anywhere in the grid
     * @param noSos - Number of times SOS has been formed
    * */
    public void isSOS(int noSos){
        int curNoSos = noSos;
        for(int[] sosPosition: sosPositions){
            if(gameState[sosPosition[0]] == gameState[sosPosition[2]] &&
            gameState[sosPosition[1]] == 1 && gameState[sosPosition[0]] == 0 ){
                noSos++;
                updateScore();
                findAndRemove(sosPosition);
                highlightSOS(sosPosition);
                isSOS(noSos);
                break;

            }
        }
        if(curNoSos == noSos){
            if(noSos % 2 == 0 && noSos != 0)
                isPlayerOne = !isPlayerOne;
            return;
        }
        isPlayerOne = !isPlayerOne;
    }

    /**
     * Method update the score of the player that formed an S-O-S
     * also update which player is currently winning
     *
     * */
    public void updateScore(){

        if(isPlayerOne){
            playerOneScore++;
            txtPlayerOneScore.setText(String.valueOf(playerOneScore));
        }
        else{
            playerTwoScore++;
            txtPlayerTwoScore.setText(String.valueOf(playerTwoScore));
        }

        if(playerOneScore > playerTwoScore)
            winningPlayer.setText("Player one is winning");
        else if (playerOneScore < playerTwoScore)
            winningPlayer.setText("Player two is winning");
        else
            winningPlayer.setText("It a draw");

    }

    /**
     * Method remove the position of the SOS that has been formed
     * from the list of SOS postions
     *
     * @param foundPosition - List of button position of the found SOS pattern
     *
     * */
    private void findAndRemove(int[] foundPosition){
        for(int i = 0; i < sosPositions.length; i++){
            if(Arrays.equals(sosPositions[i], foundPosition)){
               sosPositions[i] = new int[]{0,0,0};
                break;
            }
        }
    }

    /**
     * Method highlights the position of the found SOS
     *
     * @param sosPosition - List of button position of the found SOS pattern
     *
     * */
    private void highlightSOS(int[] sosPosition) {
        Animation animation = new AlphaAnimation(1,0);
        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(8);
        animation.setRepeatMode(Animation.REVERSE);
        for (int value : sosPosition) {
            buttons[value].setBackgroundResource(R.drawable.my_sos_buttons_bg);
            buttons[value].startAnimation(animation);
        }

    }

    /**
     * Method check if the is over
     * if it is a dialog box in displayed
     *
     * */
    private void checkGameOver(){

        for(Button btn : buttons){
            if(btn.isClickable())
                return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(SosGameActivity.this);

        builder.setCancelable(false);
        builder.setTitle("Game Over");
        String message;
        if(playerOneScore > playerTwoScore)
            message = "Player One Wins!";
        else if(playerOneScore < playerTwoScore)
            message = "Player Two Wins";
        else
            message = "It's a Draw";
        message += " \nPlayer One: " + playerOneScore + "\nPlayer Two: " + playerTwoScore;
        builder.setMessage(message);

        builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });

        builder.setPositiveButton("Play again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onRestart();
            }
        });

        builder.show();
    }

    /**
     * Method restart the game
     *
     * */
    @Override
    public void onRestart(){
        super.onRestart();
        for(Button btn : buttons){
            btn.setClickable(true);
            btn.setBackgroundResource(R.drawable.my_button_bg);
            btn.setText("");
        }
        iniData();
    }


}
