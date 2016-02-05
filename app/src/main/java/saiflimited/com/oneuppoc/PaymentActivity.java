package saiflimited.com.oneuppoc;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.stripe.android.model.Card;

import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Account;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

import saiflimited.com.oneuppoc.dialog.ErrorDialogFragment;
import saiflimited.com.oneuppoc.dialog.ProgressDialogFragment;


public class PaymentActivity extends FragmentActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */


    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        prefs = getSharedPreferences("NAME",MODE_PRIVATE);
        edit = prefs.edit();
        String customerId = prefs.getString("customerId",null);
        if(customerId!=null)
        {
            Intent intent = new Intent(getApplicationContext(),ShoppingCartActivity.class);

            startActivity(intent);
            finish();

        }


        new CallStripeService().execute();
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
    }

    public void saveCreditCard(PaymentForm form) {

        Card card = new Card(
                form.getCardNumber(),
                form.getExpMonth(),
                form.getExpYear(),
                form.getCvc());
        card.setCurrency(form.getCurrency());

        boolean validation = card.validateCard();
        if (validation) {

            edit.putString("card",new Gson().toJson(card)).commit();
            Intent intent = new Intent(getApplicationContext(),ShoppingCartActivity.class);
            startActivity(intent);


        } else if (!card.validateNumber()) {
        	handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
        	handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
        	handleError("The CVC code that you entered is invalid");
        } else {
        	handleError("The card details that you entered are invalid");
        }
    }

    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }

    private void finishProgress() {
        progressFragment.dismiss();
    }

    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }

    private TokenList getTokenList() {
        return (TokenList)(getSupportFragmentManager().findFragmentById(R.id.token_list));
    }

    class CallStripeService extends AsyncTask<String, Void, Account> {


        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected Account doInBackground(String... values) {
            com.stripe.Stripe.apiKey = "sk_test_izda8b3nMQqDUdeaQUgZpSPl";

            Map<String, Object> accountParams = new HashMap<String, Object>();
            accountParams.put("country", "US");
            accountParams.put("managed", true);
            accountParams.put("email","huzefar52@gmail.com");

            try {
                Account userAccount = Account.create(accountParams);
                return userAccount;

            } catch (AuthenticationException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (CardException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }
            return null;


        }

        @Override
        protected void onPostExecute(Account result) {

            if(result!=null)
            {
                result.getEmail();
                System.out.print(result.getId());
            }


        }
    }
}
