# Szabo Cristina-Andreea 324CA

## Tema 1 - POO
In cadrul acestui proiect am implementat un AudioPlayer care reda playlist-uri
și podcasturi, playlist-ul conținând melodii și podcastul episoade. Se pot face
diferite operatii pe aceste colecții de fișiere audio. Mai întâi citesc ce
operatii trebuie sa fac și apoi iterez prin ele și le execut.
Aceste operatii sunt:
1. Search - in functie de filtrele date am cautat în lista de fișiere audio
care se potrivesc și apoi am păstrat doar primele 5 variante corecte.
Am verificat catoate filtrele sa fie puse în același timp.
2. Select - iau din lista de rezultate de la search melodia sau podcastul
sau playlist-ul de la indicele cerut și apoi retin ce am selectat pentru
următoarele operatii
3. Load - verific tipul (playlist, podcast sau melodie) titlului selectat
și îl încarc in player cu toate caracteristicile lui.
4. AddSongsToPlaylist - verific dacă e selectat un cântec, apoi îl adaug în
playlist-ul cu indicele cerut, căutând in lista de playlist-uri toate
playlisturile unui user și selectând  playlistul potrivit. Verific apoi dacă
acel cântec e deja în playlist, iar dacă e îl scot, dacă nu, îl adaug.
5. Like - pentru aceasta comanda retin toate melodiile la care un user a dat
like intr-o lista, iar dacă melodia se afla deja în lista, o scot.
6. Repeat - verific tipul titlului selectat, iar dacă este o simpla melodie din
biblioteca și nu e într-un playlist verific starea de repeat: dacă este 0,
inseamna ca dacă remaining time ul este mai mic decât 0, nu se va mai reda nimic,
dacă este 1, la remaining time se mai aduna o data durata cântecului și dacă
este 2, se aduna durata cântecului pana remaining time ul este mai mare decât 0.
Dacă titlul selectat este al unui playlist: dacă starea de repeat e 0 playlist-ul
va rula normal, dacă este 1 și se ajunge la final, se va rula iar primul cântec
din playlist, iar dacă e 2, se va rula cântecul curent pana remaining time va
fi mai mare decât 0. Dacă este podcast, verific dacă podcastul a fost încărcat
înainte și iau starea anterioara reținută în load, apoi folosesc aceea6și
logica de la playlist.
7. Shuffle - dacă trebuie dat shuffle, sortez vectorul de melodii din playlist
in functie de indicii reieșiti din shuffle ul făcut cu seed ul cerut, păstrez
ordinea anterioara a cantecelor într o  variabila și folosesc vectorul sortat,
iar dacă revin la no shuffle, folosesc vectorul anterior. În rest rularea
playlistului se face normal, după logica de dinainte.
8. Next - am verificat poziția cântecului curent în lista de melodii din
playlist și apoi am luat cântecul următor și l am pus în player.
9. Prev - 
