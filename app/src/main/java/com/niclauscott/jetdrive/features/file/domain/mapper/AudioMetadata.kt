package com.niclauscott.jetdrive.features.file.domain.mapper

import com.niclauscott.jetdrive.features.file.data.model.dto.AudioMetadataResponseDTO
import com.niclauscott.jetdrive.features.file.domain.model.AudioMetadata

fun AudioMetadataResponseDTO.toAudioMetadata(): AudioMetadata =
    AudioMetadata(
        title = title,
        artist = artist,
        genre = genre,
        durationInSeconds = durationInSeconds,
        base64CoverArt = base64CoverArt
    )