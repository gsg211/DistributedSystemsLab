package com.sd.lab3.services

import com.sd.lab3.interfaces.TimeInterface
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class TimeService : TimeInterface {
    override fun getCurrentTime():String {
        val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(Date())
    }
}