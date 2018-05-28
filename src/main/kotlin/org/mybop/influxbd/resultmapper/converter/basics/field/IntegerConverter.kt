package org.mybop.influxbd.resultmapper.converter.basics.field

import org.mybop.influxbd.resultmapper.converter.IntegerFieldConverter
import kotlin.reflect.full.createType

class IntegerConverter : IntegerFieldConverter<Int> {

    override fun supportedType() = Int::class.createType()

    override fun convert(value: Int) = value

    override fun reverse(value: Int) = value
}
