package com.example.raymondyao.cs193a_hw7_kyguo_ryao28

import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_channel_list.*
import java.time.LocalDateTime

class ChannelListActivity : AppCompatActivity() {

    private var channelArray = ArrayList<String>()
    private lateinit var myAdapter : ArrayAdapter<String>
    private val fb = FirebaseDatabase.getInstance().reference
    private lateinit var mp: MediaPlayer

    companion object {
        var email = ""
    }

    //Function called on program start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_list)

        if (this.intent != null) {
            email = this.intent.getStringExtra("account")
        }
        val myIntent = Intent(this, ChannelActivity::class.java)
        myIntent.putExtra("account", email)

        // Sets a listener for the channel that the user clicks on
        channel_list.setOnItemClickListener { _, _, index, _ ->
            myIntent.putExtra("channel", "${channelArray[index]}")
            startActivity(myIntent)
        }
        loadChannels()
        myAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, channelArray)
        channel_list.adapter = myAdapter
    }

    // Function called to load all the preexisting channels already on the Firebase database
    private fun loadChannels() {
        val chan = fb.child("/treechat/channels")
        chan.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                for(data in data.children) {
                    channelArray.add(data.key!!)
                }
                myAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // error
            }
        })

    }

    // Function called to return the current timestamp
    private fun getTimeStamp(): String {
        return LocalDateTime.now().toString()

    }

    // Creates a new channel and adds the channel to Firebase
    fun createChannelClick(view: View) {
        mp = MediaPlayer.create(this, R.raw.explosion)
        mp.start()
        val chan = fb.child("/treechat/channels")
        var exists = false
        chan.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                for(data in data.children) {
                    if(data.key!!.toString() == channel.text.toString()) {      // Inputed channel name already exists
                        Toast.makeText(this@ChannelListActivity, "${channel.text} already exists!", Toast.LENGTH_LONG).show()
                        exists = true
                    }
                }
                if(!exists) {    // channel name does not exist, adding the new channel
                    channelArray.add(channel.text.toString())
                    myAdapter.notifyDataSetChanged()
                    val newChannel = fb.child("/treechat/channels/${channel.text}")
                    newChannel.child("name").setValue("${channel.text}")
                    newChannel.child("/messages/1").setValue("treebot")
                    newChannel.child("/messages/2").setValue("This is the ${channel.text} channel!")
                    newChannel.child("/messages/3").setValue(getTimeStamp())

                }
                channel.text.clear()
                myAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // error
            }
        })
    }

}
