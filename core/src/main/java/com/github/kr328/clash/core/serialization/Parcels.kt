package com.github.kr328.clash.core.serialization

import android.os.Parcel
import com.github.kr328.clash.core.utils.Log
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

object Parcels {
    fun <T> dump(serializer: SerializationStrategy<T>, obj: T, parcel: Parcel) {
        serializer.serialize(ParcelsEncoder(parcel), obj)
    }

    fun <T> load(deserializer: DeserializationStrategy<T>, parcel: Parcel): T {
        return deserializer.deserialize(ParcelsDecoder(parcel))
    }

    private class ParcelsEncoder(private val parcel: Parcel) :
        Encoder, CompositeEncoder {
        override val serializersModule: SerializersModule
            get() = EmptySerializersModule

        override fun encodeBooleanElement(desc: SerialDescriptor, index: Int, value: Boolean) =
            encodeBoolean(value)
        override fun encodeByteElement(desc: SerialDescriptor, index: Int, value: Byte) =
            encodeByte(value)
        override fun encodeCharElement(desc: SerialDescriptor, index: Int, value: Char) =
            encodeChar(value)
        override fun encodeDoubleElement(desc: SerialDescriptor, index: Int, value: Double) =
            encodeDouble(value)
        override fun encodeFloatElement(desc: SerialDescriptor, index: Int, value: Float) =
            encodeFloat(value)
        override fun encodeIntElement(desc: SerialDescriptor, index: Int, value: Int) =
            encodeInt(value)
        override fun encodeLongElement(desc: SerialDescriptor, index: Int, value: Long) =
            encodeLong(value)
        override fun encodeShortElement(desc: SerialDescriptor, index: Int, value: Short) =
            encodeShort(value)
        override fun encodeStringElement(desc: SerialDescriptor, index: Int, value: String) =
            encodeString(value)

        override fun <T : Any> encodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T?
        ) = encodeNullableSerializableValue(serializer, value)
        override fun <T> encodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T
        ) = encodeSerializableValue(serializer, value)

        override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = this
        override fun endStructure(descriptor: SerialDescriptor) {}

        override fun encodeBoolean(value: Boolean) =
            parcel.writeByte(if ( value ) 1 else 0)
        override fun encodeByte(value: Byte) =
            parcel.writeByte(value)
        override fun encodeChar(value: Char) =
            parcel.writeInt(value.toInt())
        override fun encodeDouble(value: Double) =
            parcel.writeDouble(value)
        override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) =
            parcel.writeInt(index)
        override fun encodeFloat(value: Float) =
            parcel.writeFloat(value)
        override fun encodeInt(value: Int) =
            parcel.writeInt(value)
        override fun encodeLong(value: Long) =
            parcel.writeLong(value)
        override fun encodeNotNullMark() =
            encodeBoolean(true)
        override fun encodeNull() =
            encodeBoolean(false)
        override fun encodeShort(value: Short) =
            parcel.writeInt(value.toInt())
        override fun encodeString(value: String) =
            parcel.writeString(value)
        
        override fun encodeInline(descriptor: SerialDescriptor): Encoder = this
        override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder = this
    }

    class ParcelsDecoder(private val parcel: Parcel) : Decoder, CompositeDecoder {
        override val serializersModule: SerializersModule
            get() = EmptySerializersModule

        override fun decodeElementIndex(descriptor: SerialDescriptor) =
            CompositeDecoder.DECODE_DONE
        override fun decodeCollectionSize(descriptor: SerialDescriptor) =
            decodeInt()
        override fun decodeSequentially() = true

        override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int) =
            decodeBoolean()
        override fun decodeByteElement(descriptor: SerialDescriptor, index: Int) =
            decodeByte()
        override fun decodeCharElement(descriptor: SerialDescriptor, index: Int) =
            decodeChar()
        override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int) =
            decodeDouble()
        override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int) =
            decodeFloat()
        override fun decodeIntElement(descriptor: SerialDescriptor, index: Int) =
            decodeInt()
        override fun decodeShortElement(descriptor: SerialDescriptor, index: Int) =
            decodeShort()
        override fun decodeLongElement(descriptor: SerialDescriptor, index: Int) =
            decodeLong()
        override fun decodeStringElement(descriptor: SerialDescriptor, index: Int) =
            decodeString()
        
        override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = this

        override fun <T : Any> decodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T?>,
            previousValue: T?
        ) = decodeNullableSerializableValue(deserializer)
        override fun <T> decodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T>,
            previousValue: T?
        ) = decodeSerializableValue(deserializer)

        override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = this
        override fun endStructure(descriptor: SerialDescriptor) {}

        override fun decodeBoolean() =
            parcel.readByte() != 0.toByte()
        override fun decodeByte() =
            parcel.readByte()
        override fun decodeChar() =
            parcel.readInt().toChar()
        override fun decodeDouble() =
            parcel.readDouble()
        override fun decodeEnum(enumDescriptor: SerialDescriptor) =
            parcel.readInt()
        override fun decodeFloat() =
            parcel.readFloat()
        override fun decodeInt() =
            parcel.readInt()
        override fun decodeLong() =
            parcel.readLong()
        override fun decodeNotNullMark() =
            decodeBoolean()
        override fun decodeNull(): Nothing? =
            null
        override fun decodeShort() =
            parcel.readInt().toShort()
        override fun decodeString() =
            parcel.readString() ?: throw NullPointerException("String null")
        
        override fun decodeInline(descriptor: SerialDescriptor): Decoder = this
    }
}


