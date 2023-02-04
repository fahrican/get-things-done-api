package com.onecosys.get_things_done.web.config

import com.onecosys.get_things_done.util.converter.StringToTaskStatusConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToTaskStatusConverter())
    }
}