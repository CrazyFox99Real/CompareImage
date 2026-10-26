# CompareLab — Android

App Android offline per confrontare due immagini o GIF. Interfaccia in italiano.

## Funzioni

- Progetti persistenti sul telefono, rinominabili ed eliminabili.
- Importazione di due file PNG/JPG/WEBP/GIF tramite selettore Android, e nomi delle immagini modificabili.
- Modalità affiancata, tenere premuto per passare da A a B (invertibile), separatore orizzontale/verticale, ghosting con opacità, differenze per pixel con colore, soglia e intensità.
- Zoom e trascinamento sincronizzati; GIF animate nel browser WebView. Le differenze su GIF vengono aggiornate durante la riproduzione.
- Funzionamento locale, senza account, pubblicità o permessi Internet.

## Generare APK tramite GitHub

1. Crea un repository GitHub vuoto e carica **tutti i file e le cartelle** di questo ZIP nella radice (inclusa `.github`).
2. Apri **Actions** > **Build Android APK** > **Run workflow** (oppure esegui un commit su `main`).
3. Al termine apri il workflow riuscito, scendi a **Artifacts** e scarica **CompareLab-debug-APK**.
4. Estrai lo ZIP scaricato e installa `app-debug.apk` su Android. Per installare APK esterni può essere necessario autorizzare l'app File/Browser in Android.

## Compilazione locale alternativa

Apri il progetto in Android Studio con JDK 17 e Android SDK 35 e usa **Build > Build APK(s)**.

## Note

- La modalità differenze confronta i pixel **dopo aver adattato ogni immagine al riquadro**. Per misurazioni accurate è consigliato usare screenshot con la stessa dimensione, stesso taglio e stesso allineamento (es. stessa vista AudioTool). Lo spostamento e lo zoom sono comuni ai due file, non correggono differenze di prospettiva o posizione tra le immagini.
- In modalità GIF, ogni animazione segue il proprio tempo di riproduzione: non è garantita la sincronizzazione dei frame.
- Le immagini originali sono salvate nel progetto nell'archiviazione privata dell'app. Rimuovere l'app cancella anche i progetti; non è ancora presente una funzione di esportazione/importazione.
- Questa versione è un MVP da compilare e testare su Android; non include test fisici su un dispositivo.
