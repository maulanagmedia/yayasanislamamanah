package co.id.gmedia.yia

import android.os.Bundle
import co.id.gmedia.yia.R
import android.view.animation.AlphaAnimation
import co.id.gmedia.yia.SlashScreenActivity
import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import co.id.gmedia.yia.LoginActivity

class SlashScreenActivity : AppCompatActivity() {
    private var ivLogo: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slash_screen)
        val animation1 = AlphaAnimation(0f, 1.0f)
        animation1.duration = 1500
        ivLogo?.alpha = 1f
        ivLogo?.startAnimation(animation1)
        if (!splashLoaded) {
            val secondsDelayed = 2
            Handler().postDelayed({ //startActivity(new Intent(SplashScreen.this, DaftarVideo.class));
                startActivity(Intent(this@SlashScreenActivity, LoginActivity::class.java))
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, secondsDelayed * 1000.toLong())

            //splashLoaded = true;
        } else {

            //Intent goToMainActivity = new Intent(SplashScreen.this, DaftarVideo.class);
            val goToMainActivity = Intent(this@SlashScreenActivity, LoginActivity::class.java)
            goToMainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(goToMainActivity)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    companion object {
        private const val splashLoaded = false
    }
}