package com.example.trelloclone.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.trelloclone.R
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloclone.adapters.BoardItemsAdapter
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var toolbar_main_activity: Toolbar
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var nav_view: NavigationView
    private lateinit var fab_create_board: FloatingActionButton
    private lateinit var rv_boards_list: RecyclerView
    private lateinit var tv_no_boards_available: TextView
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main_activity = findViewById(R.id.toolbar_main_activity)
        drawer_layout = findViewById(R.id.drawer_layout)
        nav_view = findViewById(R.id.nav_view)
        fab_create_board = findViewById(R.id.fab_create_board)
        rv_boards_list = findViewById(R.id.rv_boards_list)
        tv_no_boards_available = findViewById(R.id.tv_no_boards_available)

        setupActionBar()

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this, true)

        fab_create_board.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, Constants.CREATE_BOARD_REQUEST_CODE)
        }
    }


    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this,
                    MyProfileActivity::class.java), Constants.MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User,readBoardsList: Boolean) {
       // hideProgressDialog()
        mUserName = user.name

        // The instance of the header view of the navigation view.
        val headerView = nav_view.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        // Load the user image in the ImageView.
        Glide
            .with(this)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.name

        if (readBoardsList) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirestoreClass().loadUserData(this@MainActivity)
        } else if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.CREATE_BOARD_REQUEST_CODE) {
            FirestoreClass().getBoardsList(this)
        }  else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>){
        hideProgressDialog()
        if (boardsList.size > 0) {
            rv_boards_list.visibility = View.VISIBLE
            tv_no_boards_available.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(this@MainActivity)
            rv_boards_list.setHasFixedSize(true)

            // Create an instance of BoardItemsAdapter and pass the boardList to it.
            val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
            rv_boards_list.adapter = adapter // Attach the adapter to the recyclerView.

            adapter.setOnClickListener(object :
                BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        } else {
            rv_boards_list.visibility = View.GONE
            tv_no_boards_available.visibility = View.VISIBLE
        }
    }
}