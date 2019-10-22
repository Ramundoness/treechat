package com.example.raymondyao.cs193a_hw7_kyguo_ryao28

import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_channel.*
import java.nio.channels.Channel
import java.time.LocalDateTime

class ChannelActivity : AppCompatActivity() {

    private var messageArray = ArrayList<String>()
    private lateinit var myAdapter : ArrayAdapter<String>
    private lateinit var user: String
    private lateinit var channel: String
    private lateinit var message: String
    private val fb = FirebaseDatabase.getInstance().reference
    private var counter = 4     // counter that keeps track of current message child
    private lateinit var mp: MediaPlayer

    //Function called on program start and loads old messages
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        if (this.intent != null) {
            channel = this.intent.getStringExtra("channel")?:this.intent.getStringExtra("serviceChannel")

            channelName.text = channel
            usernameView.text = ChannelListActivity.email
            user = ChannelListActivity.email
        }
        myAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messageArray)
        messageList.adapter = myAdapter
        loadOldMessages()

        val intent = Intent(this, TreeChatService::class.java)
        intent.action = "receive"
        intent.putExtra("channel", "$channel")
        startService(intent)
    }

    //Loads old messages in selected channel
    private fun loadOldMessages() {
        val messages = fb.child("/treechat/channels/$channel/messages")
        var from = ""
        var text = ""
        messages.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                //Loads every message
                for(data in data.children) {
                    /* We implemented a number based message system (instead of using an array), as seen below.
                    1: "ramundoness28@gmail.com"
                    2: "Hi first message"
                    3: "2019-03-21T01:31:52.150"
                    4: "martystepp@gmail.com"
                    5: "This is the second message folks"
                    6: "2019-03-21T01:46:38.858"
                    ...
                     */
                    if(((data.key!!).toInt() + 2) % 3 == 0) {       // if the current counter is on the "from" tag (ie any multiple of 1 + 3n)
                        from = data.value!!.toString()
                    } else if(((data.key!!).toInt() + 1) % 3 == 0) {        // if the current counter is on the "text" tag (ie any multiple of 2 + 3n)
                        text = data.value!!.toString()
                    } else if ((data.key!!).toInt() % 3 == 0)       // adds the completed message to the array (ie any multiple of 3 + 3n)
                        messageArray.add(from + ": " + text)
                }
                myAdapter.notifyDataSetChanged()
                counter = data.children.last().key!!.toInt() + 1
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // error
            }
        })

    }

    //Returns timestamp
    private fun getTimeStamp(): String {
        return LocalDateTime.now().toString()

    }

    //Puts new user-inputed message into chat
    fun newMessage(view: View) {
        message = messageText.text.toString()
        messageArray.add(user + ": " + message)
        myAdapter.notifyDataSetChanged()
        messageText.text.clear()
        val newChannel = fb.child("/treechat/channels/$channel/messages")
        newChannel.child(counter.toString()).setValue(user)
        counter++
        newChannel.child(counter.toString()).setValue(message)
        counter++
        newChannel.child(counter.toString()).setValue(getTimeStamp())
        counter++
        mp = MediaPlayer.create(this, R.raw.pig)
        mp.start()
    }
}
