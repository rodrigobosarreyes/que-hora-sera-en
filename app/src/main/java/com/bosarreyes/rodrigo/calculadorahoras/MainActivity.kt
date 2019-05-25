package com.bosarreyes.rodrigo.calculadorahoras

import android.app.TimePickerDialog
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val CERO = "0"
    private val DOS_PUNTOS = ":"

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private lateinit var paises: Array<String>

    private val c = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentTime = getCurrentTime()

        tvCurrentTime.text = currentTime
        tvTimeZone.text = c.timeZone.displayName

        paises = resources.getStringArray(R.array.paises)

        // Nombre de los días
        val formatDias = DateFormatSymbols().weekdays
        // Nombre de los meses
        val formatMeses = DateFormatSymbols().months
        // Obtiene el nombre del día actual
        val dia = formatDias[c.get(Calendar.DAY_OF_WEEK)]
        // El número del día en el mes
        val diaNum = c.get(Calendar.DAY_OF_MONTH)
        // Número del mes
        val mesNum = c.get(Calendar.MONTH)
        // Nombre del mes
        val mes = formatMeses[mesNum]
        // Año
        val anno = c.get(Calendar.YEAR)
        tvDate.text = "$dia, $diaNum $mes, $anno"

        handler = Handler()
        val delay = 1000L

        runnable = object : Runnable {
            override fun run() {
                changeTime()
                handler.postDelayed(this, delay)
            }
        }
        runnable.run()

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paises)
        spinnerPaises.adapter = adapter


        button.setOnClickListener {
//            view -> showTimePicker()
            view -> calcHora()
        }

        buttonTimepicker.setOnClickListener {
            view -> showTimePicker()
        }
    }

    private fun showTimePicker() {
//        changeTime()
        val picker = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener{
                view, hourOfDay, minute ->
                val horas = if (hourOfDay >= 10) hourOfDay.toString() else "$CERO$hourOfDay"
                val minutos = if (minute >= 10) minute.toString() else "$CERO$minute"
                val timeInput = "$horas$DOS_PUNTOS$minutos"
                textViewHora.text = timeInput
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false)

        picker.show()
    }

    private fun changeTime() {
        var currentTime = getCurrentTime()

        tvCurrentTime.text = currentTime
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    private fun getCurrentTime(): String {
        c.time = Date()
        val hora = c.get(Calendar.HOUR_OF_DAY)
        val minuto = c.get(Calendar.MINUTE)
        val segundo = c.get(Calendar.SECOND)

        val horas = if (hora >= 10) hora.toString() else "$CERO$hora"
        val minutos = if (minuto >= 10) minuto.toString() else "$CERO$minuto"
        val segundos = if (segundo >= 10) segundo.toString() else "$CERO$segundo"

        return "$horas$DOS_PUNTOS$minutos$DOS_PUNTOS$segundos"
    }

    private fun calcHora() {
        Toast.makeText(this, BuildConfig.APIKey, Toast.LENGTH_LONG).show()
        // Obtiene el locale actual
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }
        // Obtiene el día actual
        val actualDate = getFecha(locale)
        // Obtiene la hora del tv
        val hora = textViewHora.text

        // Obtiene la fecha actual y la hora que ha elegido el usuario
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
        val inputDate = "$actualDate $hora"
        val date = inputFormat.parse(inputDate)

        val paisSeleccionado = spinnerPaisesDestino.selectedItem
        var timeZone:TimeZone = TimeZone.getDefault()

        when(paisSeleccionado) {
            "España" -> timeZone = TimeZone.getTimeZone("Europe/Madrid")
            "Guatemala" -> timeZone = TimeZone.getTimeZone("America/Guatemala")
            "México" -> timeZone = TimeZone.getTimeZone("America/Mexico_City")
            "Colombia" -> timeZone = TimeZone.getTimeZone("America/Bogota")
            "Argentina" -> timeZone = TimeZone.getTimeZone("America/Buenos_Aires")
            "Venezuela" -> timeZone = TimeZone.getTimeZone("Africa/Algiers")
        }

        val cal = Calendar.getInstance()
        cal.time = date
        val f = SimpleDateFormat("HH:mm", locale)
        f.timeZone = timeZone

//        Log.d("Calendar xd", cal.time.toString())
//        Log.d("Calendar xd", timeZone.id)
//        Log.d("Calendar default", TimeZone.getDefault().id)
//        Log.d("Calendar", f.format(cal.time))
//        Log.d("getFecha", getFecha(locale))
        textViewResultado.text = f.format(cal.time)
    }

    private fun getFecha(locale: Locale): String {
        val date = Calendar.getInstance().time
        val fecha = SimpleDateFormat("yyyy-MM-dd", locale).format(date)

        return fecha
    }
}
