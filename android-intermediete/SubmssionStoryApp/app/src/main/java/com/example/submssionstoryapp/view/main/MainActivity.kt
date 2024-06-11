package com.example.submssionstoryapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submssionstoryapp.R
import com.example.submssionstoryapp.ViewModelFactory
import com.example.submssionstoryapp.adapter.StoryAdapter
import com.example.submssionstoryapp.data.model.ListStoryItem
import com.example.submssionstoryapp.databinding.ActivityMainBinding
import com.example.submssionstoryapp.view.detail_story.DetailStoryActivity
import com.example.submssionstoryapp.view.login.LoginActivity
import com.example.submssionstoryapp.view.maps.MapsActivity
import com.example.submssionstoryapp.view.story.StoryActivity
import com.example.submssionstoryapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storiesAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        observeSession()
        observeLogout()
        setupRecyclerView()
        observeListStory()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, StoryActivity::class.java))
        }
    }

    private fun observeLogout() {
        binding.barApp.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itemMenu -> {
                    binding.barApp.setOnMenuItemClickListener(null)

                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.exit_confirm))
                        setMessage(getString(R.string.exit))
                        setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.logout()
                            binding.barApp.setOnMenuItemClickListener { _ ->
                                observeLogout()
                                true
                            }
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.dismiss()
                            binding.barApp.setOnMenuItemClickListener { _ ->
                                observeLogout()
                                true
                            }
                        }
                        create()
                        show()
                    }
                    true
                }

                R.id.menuBahasa -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.menuMaps -> {
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }
    private fun setupRecyclerView() {
        storiesAdapter = StoryAdapter(object : StoryAdapter.OnAdapterListener {
            override fun onClick(story: ListStoryItem) {
                navigateToDetailStory(story)
            }
        })
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storiesAdapter
        }
    }

    private fun observeListStory() {
        viewModel.listStory.observe(this) { pagingData ->
            storiesAdapter.submitData(lifecycle, pagingData)
        }
    }

    private fun navigateToDetailStory(story: ListStoryItem) {
        val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.EXTRA_STORY_ITEM, story)
        startActivity(intent)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}