package sk.duracik.myaiapplication.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sk.duracik.myaiapplication.data.local.PlantDao
import sk.duracik.myaiapplication.data.local.PlantEntity
import sk.duracik.myaiapplication.model.Plant

class PlantRepository(private val plantDao: PlantDao) {

    // Získanie všetkých rastlín ako Flow
    val allPlants: Flow<List<Plant>> = plantDao.getAllPlants().map { entities ->
        entities.map { PlantEntity.toPlant(it) }
    }

    // Získanie rastliny podľa ID
    suspend fun getPlant(id: Int): Plant? {
        val plantEntity = plantDao.getPlantById(id)
        return plantEntity?.let { PlantEntity.toPlant(it) }
    }

    // Pridanie novej rastliny
    suspend fun insertPlant(plant: Plant): Long {
        val plantEntity = PlantEntity.fromPlant(plant)
        return plantDao.insertPlant(plantEntity)
    }

    // Aktualizácia existujúcej rastliny
    suspend fun updatePlant(plant: Plant) {
        val plantEntity = PlantEntity.fromPlant(plant)
        plantDao.updatePlant(plantEntity)
    }

    // Vymazanie rastliny
    suspend fun deletePlant(plant: Plant) {
        val plantEntity = PlantEntity.fromPlant(plant)
        plantDao.deletePlant(plantEntity)
    }

    // Vymazanie všetkých rastlín
    suspend fun deleteAllPlants() {
        plantDao.deleteAllPlants()
    }
}
