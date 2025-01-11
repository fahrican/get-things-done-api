package com.onecosys.getthingsdone.config

import com.onecosys.getthingsdone.task.web.StringToTaskStatusConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class FormatterConfig : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToTaskStatusConverter())
    }
}
