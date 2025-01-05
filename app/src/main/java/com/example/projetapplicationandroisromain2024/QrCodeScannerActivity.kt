package com.example.projetapplicationandroisromain2024

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QrCodeScannerActivity : Activity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var dataBaseHelper: DataBaseHelper
    private var returnValue: Int = 0
    private var refItem: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        dataBaseHelper = DataBaseHelper(this)
        startQrCodeScanner()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        radioGroup = findViewById(R.id.radioGroup)
        val scanButton = findViewById<Button>(R.id.scanButton)
        scanButton.setOnClickListener {

            if (radioGroup.checkedRadioButtonId == -1) {

                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (radioGroup.checkedRadioButtonId == R.id.radioBorrow) {
                returnValue = 0
            } else if (radioGroup.checkedRadioButtonId == R.id.radioReturn) {
                returnValue = 1
            }
            Toast.makeText(this,refItem + returnValue,Toast.LENGTH_SHORT).show()
            dataBaseHelper.updateAvailabilityItem(refItem, returnValue)
            finish()
        }

    }

    @Deprecated("Deprecated in Java")
    private fun startQrCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scannez un QR code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val scannedId = result.contents
                processScannedItem(scannedId)
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processScannedItem(scannedId: String) {
        Toast.makeText(this, "Scanned ID: $scannedId", Toast.LENGTH_SHORT).show()
        refItem = scannedId

    }
}