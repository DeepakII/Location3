package com.example.location3

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.location3.ui.theme.Location3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Location3Theme {
                // A surface container using the 'background' color from the theme

                val viewModel:LocationViewModel=viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context= LocalContext.current
    val locationUtils=LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils, context = context,viewModel=viewModel)
}


@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    context: Context,
    viewModel: LocationViewModel){

    val location = viewModel.location.value


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true){
                // I have access the location
                locationUtils.requestLocationUpdate(viewModel=viewModel)
            }else{
                // Ask for the permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )|| ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if(rationaleRequired){
                    Toast.makeText(context,"Location Permission is Required",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context,"Give Location Permission Manually",Toast.LENGTH_SHORT).show()
                }
            }
        })

     Column(modifier = Modifier.fillMaxSize(),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center) {
         
         if(location!=null){
                Text(text = "Address: Latitude-> ${location.latitude} & Longitude-> ${location.longitude}")
         }
         else{
             Text(text = "Location not available ")
         }

         Button(onClick = {
             if(locationUtils.hasLocation(context)){
                //Permission Already Granted .. Update the location
                 locationUtils.requestLocationUpdate(viewModel=viewModel)
             }else{
                // Request location permission
                 requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))
             }
         }) {
             Text(text = "Get Location")
         }
         
     }
}

