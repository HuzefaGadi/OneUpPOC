package saiflimited.com.oneuppoc;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    String token;
    String customerId;
    public static final String PUBLISHABLE_KEY = "pk_test_as2kDza0fjIBO0fgsD1aru3j";
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("NAME", MODE_PRIVATE);
        edit = prefs.edit();

        Card card = new Gson().fromJson(prefs.getString("card",null),Card.class);
        if(card!=null)
        {
            new Stripe().createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {

                            new CallStripeService().execute(token.getId());
                        }
                        public void onError(Exception error) {

                        }
                    });


        }

    }

    class CallStripeService extends AsyncTask<String, Void, Charge> {


        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected Charge doInBackground(String... values) {

            com.stripe.Stripe.apiKey = "sk_test_izda8b3nMQqDUdeaQUgZpSPl";

// Get the credit card details submitted by the form
            String token = values[0];

// Create the charge on Stripe's servers - this will charge the user's card
            try {
                Map<String, Object> chargeParams = new HashMap<String, Object>();
                chargeParams.put("amount", 1000); // amount in cents, again
                chargeParams.put("currency", "usd");
                chargeParams.put("source", token);
                chargeParams.put("description", "Example charge");

                Charge charge = Charge.create(chargeParams);


                return charge;
            } catch (CardException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
            return null;


        }

        @Override
        protected void onPostExecute(Charge result) {

            if(result!=null)
            {
                System.out.print(result.getId());
            }


        }
    }

}
