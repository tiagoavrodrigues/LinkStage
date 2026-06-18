package turmaA.grupoB.LinkStage

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import turmaA.grupoB.LinkStage.ui.navigation.AppNavigation
import turmaA.grupoB.LinkStage.ui.theme.LinkStageTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkStageTheme {
                AppNavigation()
            }
        }
    }
}
