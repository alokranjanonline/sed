package com.example.sed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdView
import org.json.JSONObject

class SchemenameActivity : AppCompatActivity() {
    private var number: Int = 0
    private lateinit var  mAdView : AdView
    private var list:ArrayList<ItemsViewModel> = ArrayList()
    val adapter = CustomAdapter1(list,this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schemename)
        mAdView = findViewById(R.id.adView)
        val actionbar=supportActionBar
        val textShow_error_msg: TextView = findViewById(R.id.textErrorDisplay)
        val textShow_Internet_msg: TextView = findViewById(R.id.internetAvailability)
        //val intet: Intent
        val str = intent.getIntExtra("schemeId",1)
        actionbar!!.title=intent.getStringExtra("schemeName")
        //actionbar.setDisplayHomeAsUpEnabled(true)
        val stateId = intent.getIntExtra("stateId", 0)

        //textShow_error_msg.text=str
        if (str != null) {
            fetch_data(str,stateId)
        }else{
            textShow_error_msg.text="Nothing found here. Please check again."
        }

        //Coding for RecycleVIew
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter


        //Swipe to refresh
        swipeToRefresh(recyclerview)
        show_banner_ads(mAdView,this)

    }
    fun swipeToRefresh(recyclerview:RecyclerView){
        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            val textShow_error_msg = findViewById<TextView>(R.id.textErrorDisplay)
            textShow_error_msg.text = number++.toString()
            recyclerview.setAdapter(adapter)
            swipeRefreshLayout.isRefreshing = false
        }
    }
    fun fetch_data(str:Int,state_id:Int){
        val queue = Volley.newRequestQueue(this)
        val url = "http://springtown.in/test/fetch_scheme_name.php?scheme_id="+str+"&state_id="+state_id
        val textShow_error_msg = findViewById<TextView>(R.id.textErrorDisplay)
        //textShow_error_msg.text=str+"====="+state_id
        val stringRequest = StringRequest( Request.Method.GET, url,
            { response ->
                textShow_error_msg.text = "Response is: ${url}"
                val jsonObject= JSONObject(response)
                if(jsonObject.get("response").equals("sucess")){
                    val jsonArray=jsonObject.getJSONArray("data")
                    for(i in 0.. jsonArray.length()-1){
                        val jo=jsonArray.getJSONObject(i)
                        val scheme_id=jo.get("scheme_id").toString()
                        val scheme_name=jo.get("scheme_name").toString()
                        val scheme_image=jo.get("scheme_image").toString()
                        val image_url="http://springtown.in/test/images/"+scheme_image
                        val scheme_url=jo.get("scheme_url").toString()
                        val user=ItemsViewModel(scheme_name,scheme_id,image_url,scheme_url,"Abc")
                        list.add(user)
                    }
                    adapter.notifyDataSetChanged()
                }else{
                    Toast.makeText(this, "There is some problem", Toast.LENGTH_SHORT).show()
                }
            },
            { textShow_error_msg.text = "There is some problem. Please try again." })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

}