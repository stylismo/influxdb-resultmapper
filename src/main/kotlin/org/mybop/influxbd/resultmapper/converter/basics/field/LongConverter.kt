package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.LongFieldConverter
import kotlin.reflect.full.createType

class LongConverter : LongFieldConverter<Long> {
    override fun supportedType() = Long::class.createType()

    override fun convert(value: Long) = value

    override fun reverse(value: Long) = value
}
