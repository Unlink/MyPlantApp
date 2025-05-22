package sk.duracik.myaiapplication.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sk.duracik.myaiapplication.data.local.PlantDao
import sk.duracik.myaiapplication.data.local.PlantEntity
import sk.duracik.myaiapplication.data.local.PlantImageDao
import sk.duracik.myaiapplication.data.local.PlantImageEntity
import sk.duracik.myaiapplication.data.local.WateringDao
import sk.duracik.myaiapplication.data.local.WateringEntity
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.model.Watering
import java.time.LocalDate

class PlantRepository(
    private val plantDao: PlantDao,
    private val plantImageDao: PlantImageDao,
    private val wateringDao: WateringDao
) {

    // Získanie všetkých rastlín ako Flow
    val allPlants: Flow<List<Plant>> = plantDao.getAllPlants().map { plantEntities ->
        plantEntities.map { plantEntity ->
            // Pre každú rastlinu získame zoznam URL adries jej obrázkov
            val images = plantImageDao.getImagesForPlantSync(plantEntity.id)
            val imageUrls = images.map { it.imageUrl }

            // Získame záznamy o zalievaní pre túto rastlinu
            val wateringEntities = wateringDao.getWateringsForPlant(plantEntity.id)
            val wateringRecords = wateringEntities.map { WateringEntity.toWatering(it) }

            // Vytvoríme rastlinu so zoznamom URL adries a záznamami o zalievaní
            PlantEntity.toPlant(plantEntity, imageUrls, wateringRecords)
        }
    }

    // Získanie rastliny podľa ID
    suspend fun getPlant(id: Int): Plant? {
        val plantEntity = plantDao.getPlantById(id)
        return plantEntity?.let {
            // Získame zoznam URL adries obrázkov pre túto rastlinu
            val images = plantImageDao.getImagesForPlantSync(it.id)
            val imageUrls = images.map { image -> image.imageUrl }

            // Získame záznamy o zalievaní pre túto rastlinu
            val wateringEntities = wateringDao.getWateringsForPlant(it.id)
            val wateringRecords = wateringEntities.map { entity -> WateringEntity.toWatering(entity) }

            // Vytvoríme rastlinu so zoznamom URL adries a záznamami o zalievaní
            PlantEntity.toPlant(it, imageUrls, wateringRecords)
        }
    }

    // Pridanie novej rastliny
    suspend fun insertPlant(plant: Plant): Long {
        // Najprv vložíme základné údaje o rastline
        val plantEntity = PlantEntity.fromPlant(plant)
        val plantId = plantDao.insertPlant(plantEntity).toInt()

        // Potom vložíme obrázky pre túto rastlinu
        if (plant.imageUrls.isNotEmpty()) {
            val imageEntities = plant.imageUrls.mapIndexed { index, url ->
                PlantImageEntity(
                    plantId = plantId,
                    imageUrl = url,
                    sortOrder = index
                )
            }
            plantImageDao.insertImages(imageEntities)
        }

        return plantId.toLong()
    }

    // Aktualizácia existujúcej rastliny
    suspend fun updatePlant(plant: Plant) {
        // Aktualizujeme základné údaje o rastline
        val plantEntity = PlantEntity.fromPlant(plant)
        plantDao.updatePlant(plantEntity)

        // Vymažeme staré obrázky a vložíme nové
        plantImageDao.deleteImagesForPlant(plant.id)

        if (plant.imageUrls.isNotEmpty()) {
            val imageEntities = plant.imageUrls.mapIndexed { index, url ->
                PlantImageEntity(
                    plantId = plant.id,
                    imageUrl = url,
                    sortOrder = index
                )
            }
            plantImageDao.insertImages(imageEntities)
        }
    }

    // Vymazanie rastliny
    suspend fun deletePlant(plant: Plant) {
        val plantEntity = PlantEntity.fromPlant(plant)
        plantDao.deletePlant(plantEntity)
        // Obrázky sa vymažú automaticky vďaka CASCADE vymazávaniu
    }

    // Vymazanie všetkých rastlín
    suspend fun deleteAllPlants() {
        plantDao.deleteAllPlants()
        // Obrázky sa vymažú automaticky vďaka CASCADE vymazávaniu
    }

    // Pridanie obrázka k rastline
    suspend fun addPlantImage(plantId: Int, imageUrl: String) {
        // Zistíme aktuálny počet obrázkov pre správny sortOrder
        val images = plantImageDao.getImagesForPlantSync(plantId)
        val sortOrder = images.size
        val imageEntity = PlantImageEntity(
            plantId = plantId,
            imageUrl = imageUrl,
            sortOrder = sortOrder
        )
        plantImageDao.insertImage(imageEntity)
    }

    // Odstránenie obrázka podľa URL
    suspend fun removePlantImage(plantId: Int, imageUrl: String) {
        val images = plantImageDao.getImagesForPlantSync(plantId)
        val imageToDelete = images.find { it.imageUrl == imageUrl }
        imageToDelete?.let {
            plantImageDao.deleteImage(it)
        }
    }

    // Pridanie záznamu o zalievaní rastliny
    suspend fun addWatering(plantId: Int) {
        // Vytvorenie nového záznamu o zalievaní
        val wateringEntity = WateringEntity(
            plantId = plantId,
            date = LocalDate.now().toString()
        )

        // Uloženie záznamu o zalievaní do databázy
        wateringDao.insertWatering(wateringEntity)
    }

    // Získanie všetkých záznamov o zalievaní pre konkrétnu rastlinu
    suspend fun getWateringsForPlant(plantId: Int): List<Watering> {
        val wateringEntities = wateringDao.getWateringsForPlant(plantId)
        return wateringEntities.map { WateringEntity.toWatering(it) }
    }

    // Získanie ID pre nový záznam o zalievaní
    private suspend fun getNextWateringId(plant: Plant): Int {
        return plant.wateringRecords.maxOfOrNull { it.id }?.plus(1) ?: 1
    }

}
