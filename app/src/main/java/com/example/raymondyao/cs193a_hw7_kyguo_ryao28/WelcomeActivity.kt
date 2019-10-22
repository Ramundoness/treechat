// Kevin Guo <kyguo@stanford.edu>, Raymond Yao <ryao28@stanford.edu>
// CS 193A, Winter 2019 (instructor: Marty Stepp)
// Homework Assignment 7
// This program is a multi-user chatting system where users can
// join chat channels and send messages to each other.
// Extra features: Added sound effects (creating a new channel, creating a new message)
package com.example.raymondyao.cs193a_hw7_kyguo_ryao28

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var REQUEST_SIGN_IN_GOOGLE = 8888
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mp: MediaPlayer

    //Function called on program start and sets up Google Sign in
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Sets up the user credentials for a Google based-log in server
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, options)

        auth = FirebaseAuth.getInstance()

        // Assigns an on click listener to the Google sign-in button
        sign_in_button.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_SIGN_IN_GOOGLE)
        }

    }

    // Function called upon the user exiting from the Google log-in page, determining whether to move on to Channel Activity
    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_SIGN_IN_GOOGLE) {
            if (resultCode == Activity.RESULT_OK) {
                // successfully signed in
                val account = GoogleSignIn.getSignedInAccountFromIntent(intent).result!!
                val myIntent = Intent(this, ChannelListActivity::class.java)
                myIntent.putExtra("account", account.email.toString())
                startActivity(myIntent)
            } else {
                // sign-in failed
                Toast.makeText(this, "Sign in failed. Please try again.", Toast.LENGTH_LONG).show()

            }
        }
    }
}
