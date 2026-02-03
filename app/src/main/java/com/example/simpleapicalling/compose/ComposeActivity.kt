package com.example.simpleapicalling.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshyzoappmodule.R
import com.example.simpleapicalling.compose.ui.theme.FreshyzoappmoduleTheme

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

          enableEdgeToEdge()

               setContent {
                   Scaffold(modifier = Modifier
                       .fillMaxSize()
                       .systemBarsPadding())
                   {innerPadding ->

                       FreshyzoappmoduleTheme {
                           sheetDesign(modifier = Modifier.padding(innerPadding))
                       }
                   }

               }



    }
}



// card view design


@Composable

fun sheetDesign(modifier: Modifier){

    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxWidth().height(300.dp).padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        //border = BorderStroke


    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)


        ) {

            Image(painter = painterResource(R.drawable.user),
                "",
                  modifier = Modifier
                      .padding(top= 20.dp)
                      .size(50.dp)
            )
            Spacer(modifier = Modifier.fillMaxWidth()
                .size(10.dp)
            )

            Divider()

            Text("User Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold, color = Color.Black,
                modifier = Modifier.padding(5.dp))


            Row (

                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically



            )
            {

                Image(painter = painterResource(R.drawable.notification_),
                    "",
                    modifier = Modifier
                        .padding(top= 10.dp)
                        .size(20.dp)
                )



                Text("Notification ",
                    modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

            }

            Row (

                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically



            )
            {

                Image(painter = painterResource(R.drawable.logo),
                    "",
                    modifier = Modifier
                        .padding(top= 10.dp)
                        .size(40.dp)
                )





                Text(" Current Company ",
                    modifier = Modifier.padding(start = 5.dp, top = 10.dp)
                        .clickable{
                            Toast.makeText(context, "Instagram is clicked", Toast.LENGTH_SHORT).show()

                        },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

            }



            Button(onClick = {

                Toast.makeText(context,"button Clicked log Out ",Toast.LENGTH_SHORT).show()
            },
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)


            ) {
                Text("Logout",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(5.dp)



                )
            }








        }




    }


}







@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login Screen",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

































