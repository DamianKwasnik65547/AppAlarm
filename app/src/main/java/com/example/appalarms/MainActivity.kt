package com.example.appalarms

import android.app.AlarmManager //umożliwia planowanie i zarządzanie alarmami
import android.app.PendingIntent //(intent-zamiar)obiekt który reprezentuje zamiar wykonywania określonego zadania w przyszłości
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//Ta klasa reprezentuje główna aktywnosc
class MainActivity : AppCompatActivity() {
    private lateinit var timeTextView: TextView
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private lateinit var alarmListView: ListView
    private lateinit var alarmListAdapter: ArrayAdapter<String>
    private val alarmList: ArrayList<String> = ArrayList()
    // Metoda cyklu zycia wywoływana podczas tworzenia aktywności.
    // Inicjalizuje widoki i ustawia obsługę zdarzeń kliknięcia dla przycisku ustawiania alarmu.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //inicjalizuje aktywność i wykonuje konfiguracje podstawowe wymagane przez system Android
        setContentView(R.layout.activity_main)//ustawienie widoku
//te linie inicjalizuja widoki
        timeTextView = findViewById(R.id.timeTextView)
        val setAlarmButton: Button = findViewById(R.id.setAlarmButton)
        //inicjalizacja ListView i przypisanie adaptera ktory korzysta z listy alarmów (adapter słuzy do wiazanai danych z  UI)
        alarmListView = findViewById(R.id.alarmListView)
        alarmListAdapter = ArrayAdapter(this, R.layout.alarm_item, R.id.alarmTimeTextView, alarmList)
        alarmListView.adapter = alarmListAdapter
//ustawienie obsługi zdarzen klikniecia na elementach ListView
        alarmListView.setOnItemClickListener { parent, view, position, id -> //reprezentowanie rodzica po kliknieciu(alarmListView) view reprezentuje
            val selectedTime = alarmList[position]                           //widon na ktorym było klikniecie. position-pozycja elemntu w liscie
            // reakcja na kliknięcie elementu listy alarmListView, pobranie danych z wybranego elementu i wyświetlenie komunikatu z tymi danymi na ekranie.
            Toast.makeText(this, "Selected alarm: $selectedTime", Toast.LENGTH_SHORT).show()
        }
// obłsuga przetrzymania elementu listy alarmów
        alarmListView.setOnItemLongClickListener { parent/*parametr parent moge zastapic _ poniewaz nie jest istotny*/, view, position, id ->
            val selectedTime = alarmList[position]
            removeAlarmFromList(selectedTime)
            true
        }
//inicjalizacja AlarmManager i PendingInten dla AlarmReceivers
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
// Tutaj obłsugujemy klikniecie przycisku ustawienia alarmu
        setAlarmButton.setOnClickListener {
            showTimePickerDialog()
        }
    }
    // ta funkcja wyswietla dialog wyboru czasu
    //Metoda wyświetlająca dialog wyboru czasu. Pobiera bieżący czas i tworzy dialog TimePickerDialog,
    // który umożliwia użytkownikowi wybranie godziny i minuty alarmu.
    // Po wybraniu czasu, metoda aktualizuje TextView z wybranym czasem, ustawia alarm i dodaje go do listy alarmów.
    //Tworzy TimePickerDialog, który umożliwia użytkownikowi wybranie godziny i minuty alarmu.
    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance() // pobranie biezacego czasu
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
//umozliwienie uzytkownikowi wyboru godziny i minuty
        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)

                updateTimeTextView(selectedTime) //aktualizacja TextVoew z wybanym czasem
                setAlarm(selectedTime) // ustawienie alarmu metoda setAlarm
                addAlarmToList(selectedTime)//dodanie alarmu do listy alarmow
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
    // Aktualizacja widoku TextView z wybranym czasem
    //Metoda aktualizująca TextView z wybranym czasem alarmu.
    private fun updateTimeTextView(calendar: Calendar) {
     //konwertowanie obiektu calndar na tekst
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        //ustawia ciag tekstu jako TextView
        val selectedTime = timeFormat.format(calendar.time)
        timeTextView.text = selectedTime
    }
    // Ustawianie alarmu przy uzyciu AlarmMAnagera
//Metoda ustawiająca alarm za pomocą AlarmManagera. Przyjmuje czas alarmu w postaci obiektu
// Calendar i używa AlarmManagera do ustawienia powiadomienia na tę konkretną godzinę
    private fun setAlarm(alarmTime: Calendar) {
        //wywolanie set na  obiekcie AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,//wybudzenie z uspienia
            alarmTime.timeInMillis,//czas na wyołanie alarmu
            alarmIntent // // operacja do wykonania po czasie wywołania (przekazanie do metody onReceive_
        )
    }
    // Metoda dodająca alarm do listy alarmów. Przyjmuje czas alarmu w postaci obiektu Calendar,
// formatuje go jako ciąg tekstowy i dodaje do listy alarmów. Następnie odświeża adapter listy,
// aby zaktualizować widok ListView.
    private fun addAlarmToList(calendar: Calendar) {
        //konwertowanie obiektu calendar na ciag teekstowy
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val selectedTime = timeFormat.format(calendar.time)
        alarmList.add(selectedTime)//dodanie ciagu tekstu do listy
        alarmListAdapter.notifyDataSetChanged() //wywolanie metody by zaaktualizowac TekstView
    }
    // usuwanie alarmu z listy
// Metoda usuwająca alarm z listy alarmów. Przyjmuje czas alarmu jako ciąg
// tekstowy i usuwa go z listy alarmów. Następnie odświeża adapter listy,
// aby zaktualizować widok ListView i wyświetla krótkie powiadomienie o usunięciu alarmu.
    //removeAlarmFromList jest prywatną metodą odpowiedzialną za usuwanie alarmu z listy
    private fun removeAlarmFromList(selectedTime: String) {
        alarmList.remove(selectedTime)//usuwanie z listy (usuwa wybrany czas)
        alarmListAdapter.notifyDataSetChanged() // wywłanie metody aby zaktualizowac ListView
        Toast.makeText(this, "Alarm usunięty", Toast.LENGTH_SHORT).show()//informacja o usunieciu
    }
   // deleteAlarm jest publiczną funkcją, która wywołuje tę metodę po kliknięciu przycisku "Usuń".
    fun deleteAlarm(view: View) {
        val parent = view.parent as View//pobranie widoku nadrzednego
        val alarmTextView = parent.findViewById<TextView>(R.id.alarmTimeTextView)//odszukanie TextView ktory zawiera czas alarmu
        val selectedTime = alarmTextView.text.toString()//pobranie czasu jako ciag tekstu
        removeAlarmFromList(selectedTime)//wyołanie metody w celu usuniecia z listy
    }
}
