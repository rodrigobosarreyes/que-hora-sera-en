package com.bosarreyes.rodrigo.calculadorahoras

import android.app.TimePickerDialog
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import com.bosarreyes.rodrigo.calculadorahoras.dao.DBHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val CERO = "0"
    private val DOS_PUNTOS = ":"

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private val c = Calendar.getInstance()
    private val delay = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentTime = getCurrentTime()

        tvCurrentTime.text = currentTime
        tvTimeZone.text = c.timeZone.displayName

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

        runnable = object: Runnable {
            override fun run() {
                changeTime()
                handler.postDelayed(this, delay)
            }
        }
        runnable.run()

        button.setOnClickListener {
            if(textViewHora.text != "HH:MM")
                calcHora()
        }

        buttonTimepicker.setOnClickListener {
            showTimePicker()
        }

        // Carga la base de datos
        runOnUiThread {
            Runnable {
                val db = DBHelper(this)
                val wDB = db.writableDatabase
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, db.getTodasCiudades(wDB))
                spinnerPaises.adapter = adapter
                spinnerPaisesDestino.adapter = adapter
                spinnerPaises.setSelection(adapter.getPosition(db.getCiudadTimezone(db.readableDatabase, TimeZone.getDefault().id)))
                db.close()
            }.run()
        }
    }

    private fun showTimePicker() {
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
        val currentTime = getCurrentTime()

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
        // Obtiene el locale actual
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }
        // Obtiene el día actual
        val actualDate = getFecha(locale)
        // Obtiene la hora del tv (hora del país de origen)
        val hora = textViewHora.text

        // Creamos un nuevo Date con la fecha y hora establecida por el usuario
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
        val inputDate = "$actualDate $hora"
        val date = inputFormat.parse(inputDate)

        val paisSeleccionado = spinnerPaisesDestino.selectedItem.toString()
        val timeZone: TimeZone = getTimezone(paisSeleccionado)

        val cal = Calendar.getInstance()
        cal.time = date
        val f = SimpleDateFormat("HH:mm", locale)
        f.timeZone = timeZone

        textViewResultado.text = f.format(cal.time)
    }

    private fun getTimezone(paisSeleccionado: String): TimeZone {
        val db = DBHelper(this)
        val timezone = db.getTimezoneCiudad(db.writableDatabase, paisSeleccionado)

        return TimeZone.getTimeZone(timezone)
    }

    /**
     * Obtiene la fecha actual y la devuelve en un string con el formato yyyy-MM-dd
     * @return fecha formateada
     */
    private fun getFecha(locale: Locale): String {
        val date = Calendar.getInstance().time
        val fecha = SimpleDateFormat("yyyy-MM-dd", locale).format(date)

        return fecha
    }
}
