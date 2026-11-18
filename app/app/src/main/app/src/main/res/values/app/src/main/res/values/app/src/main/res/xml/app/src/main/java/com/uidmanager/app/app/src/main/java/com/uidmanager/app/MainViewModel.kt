package com.uidmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val entryDao = db.entryDao()
    private val settingsDao = db.settingsDao()

    val entries: StateFlow<List<Entry>> = entryDao.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _defaultPassword = MutableStateFlow("")
    val defaultPassword: StateFlow<String> = _defaultPassword.asStateFlow()

    private val _activeRowId = MutableStateFlow<Long?>(null)
    val activeRowId: StateFlow<Long?> = _activeRowId.asStateFlow()

    init {
        viewModelScope.launch {
            _defaultPassword.value = settingsDao.get("default_password")?.value ?: ""
        }
    }

    fun setDefaultPassword(pwd: String) {
        viewModelScope.launch {
            val existing = settingsDao.get("default_password")
            if (existing == null) {
                settingsDao.insert(Settings("default_password", pwd))
            } else {
                settingsDao.update(existing.copy(value = pwd))
            }
            _defaultPassword.value = pwd
        }
    }

    fun createNewRow() {
        viewModelScope.launch {
            val newRow = Entry(
                uid = "",
                password = _defaultPassword.value,
                cookies = ""
            )
            val id = entryDao.insert(newRow)
            _activeRowId.value = id
        }
    }

    fun updateRow(entry: Entry) {
        viewModelScope.launch { entryDao.update(entry) }
    }

    fun deleteRow(id: Long) {
        viewModelScope.launch { entryDao.delete(id) }
    }

    suspend fun fillActiveRow(uid: String?, cookies: String?): Boolean {
        val activeId = _activeRowId.value ?: return false
        val entry = entryDao.getById(activeId) ?: return false

        val updated = entry.copy(
            uid = uid ?: entry.uid,
            cookies = cookies ?: entry.cookies
        )
        entryDao.update(updated)

        val complete = updated.uid.isNotBlank() &&
                updated.cookies.isNotBlank() &&
                updated.password.isNotBlank()

        if (complete) {
            val next = Entry(
                uid = "",
                password = _defaultPassword.value,
                cookies = ""
            )
            val nextId = entryDao.insert(next)
            _activeRowId.value = nextId
        }
        return complete
    }

    fun setActiveRowId(id: Long?) {
        _activeRowId.value = id
    }
}
