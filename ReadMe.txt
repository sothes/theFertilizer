Installationshinweise zur lokalen Nutzung

Anpassen in src/bean/EDbean.java
Die WebAnwendung nutzt einen Webservice zum Lösen der Optimierungsaufgabe. Dessen URL ist einzustellen
	public static final String 	WebService	= "http://194.95.44.187:8008";
Die Daten Ihrer Modelle werden in xml Dateien gespeichert. Das Verzeichnis in dem diese Dateien liegen ist anzupassen
	public static final String ModelDir 		= "H:/eclipse_Luna/CakeEvent/testDir/";
Der Optimierer benötigt eine Modellbeschreibung. Dies ist die Datei cakeEvent.cmpl. Passen sie den Pfad an Ihre Installation an.
	public static final String CmplModel 		= "H:/eclipse_Luna/CakeEvent/cmpl/cakeEvent.cmpl";
	
Anpassen in WebContent/log4j.properties
Die Webanwendung schreibt einen Logfile. Dieser Filename ist an Ihre installation anzupassen.
	log4j.appender.LOGFILE.File=H:/eclipse/OrModel1/CakeEvent/cakeEvent.log

In den Namen, die dem Solver übergeben werden, dürfen weder Umlaute noch Blancs enthalten sein. andernfalls liefert der Solver einen Fehler.
Dies ist zu überprüfen.

Deployment auf die Turku:
Legen Sie auf der Tuku in Ihrem HomeVerzeichnis ein Verzeichnis an, in dem die Webanwendung ihre Daten speichern kann.
In diesem Verzeichnis muss Other Schreibrechte haben.
Passen sie die Konstanten ModelDir und CmplModel entsprechend an.
Passen Sie bitte auch den Eintrag in WebContent/log4j.properties an.
Bitte verwenden Sie immer absolute Dateinamen.
---------------------------------------------------------------------------------------------------------------------------------------------------------

Installationshinweise zur entfernten (remote) Nutzung

Gehen sie in Eclipse auf das Kontext-Memue des Projektes CakeEvent und erstellen Sie über
export -> war File einen war file und speichern Sie diesen in Ihrem lokalen Dateisystem.

Bitte benennen sie die war Datei um und geben sie ihr einen für Ihre Gruppe eindeutigen Namen.

Bitte rufen sie im vpn den Glassfish Administrator turku.wi-bw.tfh-wildau.de:4848 auf und melden sich dort an.
Die Anmeldedaten werden in der Veranstaltung bekannt gegeben.

Unter dem Punkt Applikationen können Sie Ihre war Datei in den Glassfish Administrator hochladen (deployen).
Dort können  Sie ihre Anwendung auch starten.
Danach ist Ihre Applikation unter  turku.wi-bw.tfh-wildau.de/warFileName:8080 auch ausserhalb des vpn verfügbar.
	
Da alle Gruppen auf dem gleichen Glassfish Administrator arbeiten ist es wichtig, das jede Gruppe ihre war Datei individuell benennt,
anderfalls können Kollisionen mit anderen Gruppen entstehen. 	