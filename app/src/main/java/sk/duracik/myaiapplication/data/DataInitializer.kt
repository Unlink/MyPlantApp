package sk.duracik.myaiapplication.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.data.repository.PlantRepository
import sk.duracik.myaiapplication.model.Plant
import java.time.LocalDate

/**
 * Trieda pre prepopuláciu databázy ukážkovými dátami pri prvom spustení aplikácie
 */
class DataInitializer(
    private val repository: PlantRepository,
    private val scope: CoroutineScope
) {
    /**
     * Skontroluje, či už máme dáta v databáze a ak nie, naplní ju ukážkovými dátami
     */
    fun populateDatabase() {
        scope.launch {
            val allPlants = repository.allPlants

            // Použijeme first() na získanie hodnoty z flow
            allPlants.collect { plantList ->
                // Ak je zoznam rastlín prázdny, pridáme ukážkové dáta
                if (plantList.isEmpty()) {
                    Log.d("DataInitializer", "Populujem databázu ukážkovými dátami")
                    prePopulateDatabase()
                } else {
                    Log.d("DataInitializer", "Databáza už obsahuje ${plantList.size} rastlín")
                }

                // Stačí nám jedna hodnota, môžeme ukončiť collect
                return@collect
            }
        }
    }

    /**
     * Naplní databázu ukážkovými dátami
     */
    private suspend fun prePopulateDatabase() {
        // Ukážkové rastliny pre prepopuláciu databázy
        val samplePlants = listOf(
            Plant(
                id = 0, // ID bude vygenerované databázou
                name = "Aloe Vera",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921",
                    "https://images.unsplash.com/photo-1509423350716-97f9360b4e09",
                    "https://images.unsplash.com/photo-1603436326446-58a9002a0a13"
                ),
                dateAdded = LocalDate.now().minusDays(120),
                description = "Aloe vera je sukulentná rastlina, ktorá sa používa v tradičnej medicíne už tisíce rokov. Jej gél má upokojujúce a hojivé účinky na pokožku a pomáha pri popáleninách či odreninách."
            ),
            Plant(
                id = 0,
                name = "Monstera",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1614594075929-b4b3bcc43665",
                    "https://images.unsplash.com/photo-1637967886160-fd0748161114",
                    "https://images.unsplash.com/photo-1636901942218-0ddd36c86eb6"
                ),
                dateAdded = LocalDate.now().minusDays(45),
                description = "Monstera deliciosa, známa ako švajčiarsky syr rastlina pre jej charakteristické diery v listoch, je populárna izbová rastlina, ktorá dodáva interiéru tropický vzhľad. Je nenáročná na údržbu a rýchlo rastie."
            ),
            Plant(
                id = 0,
                name = "Kaktus",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a",
                    "https://images.unsplash.com/photo-1551888419-7b7a520fe0ca",
                    "https://images.unsplash.com/photo-1485955900006-10f4d324d411"
                ),
                dateAdded = LocalDate.now().minusDays(280),
                description = "Kaktusy sú sukulentné rastliny, ktoré sa vyznačujú schopnosťou zadržiavať vodu v rôznych častiach svojho tela. Pochádzajú prevažne z púštnych oblastí a sú odolné voči suchu. Ideálne pre zaneprázdnených pestovateľov."
            ),
            Plant(
                id = 0,
                name = "Fikus",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1602923668104-8f9e03e77e62",
                    "https://images.unsplash.com/photo-1599598177991-ec67b5c37318",
                    "https://images.unsplash.com/photo-1627472839507-f775954a5555"
                ),
                dateAdded = LocalDate.now().minusDays(10),
                description = "Fikus je obľúbená izbová rastlina s lesklými zelenými listami. Je symbolom prosperity a hojnosti. Pre správny rast potrebuje stabilné podmienky - nemá rád časté premiestňovanie a prudké zmeny teploty."
            ),
            Plant(
                id = 0,
                name = "Orchidea",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1566907225712-46a63eda4481",
                    "https://images.unsplash.com/photo-1524598171353-ce84a157ed05",
                    "https://images.unsplash.com/photo-1610776763760-7d17c017642c"
                ),
                dateAdded = LocalDate.now().minusDays(75),
                description = "Orchidea je elegantná kvetina s exotickým vzhľadom, ktorá dokáže kvitnúť niekoľko mesiacov. Existuje viac ako 28 000 druhov orchideí, čo z nich robí jednu z najrozmanitejších rodín rastlín na Zemi."
            ),
            Plant(
                id = 0,
                name = "Sukulent",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1446071103084-c257b5f70672",
                    "https://images.unsplash.com/photo-1520302630591-fd1c66edc19d",
                    "https://images.unsplash.com/photo-1509937528035-ad76254b0356"
                ),
                dateAdded = LocalDate.now().minusDays(190),
                description = "Sukulenty sú rastliny, ktoré zadržiavajú vodu v listoch a stonkách. Sú obľúbené pre svoju nenáročnosť a rozmanitosť tvarov. Ideálne pre začínajúcich pestovateľov, keďže vyžadujú minimálnu starostlivosť."
            )
        )

        // Uloženie vzorových rastlín do databázy
        samplePlants.forEach { plant ->
            repository.insertPlant(plant)
        }

        Log.d("DataInitializer", "Databáza úspešne naplnená ${samplePlants.size} rastlinami")
    }
}
