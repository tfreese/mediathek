// Created: 13 Aug. 2025
package de.freese.player.test.flac;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc9639.html#name-file-level-metadata">file-level-metadata</a>
 *
 * @author Thomas Freese
 * @see org.jaudiotagger.audio.flac.metadatablock.BlockType
 */
public enum BlockType {
    STREAMINFO(0),
    PADDING(1),
    APPLICATION(2),
    SEEKTABLE(3),
    VORBIS_COMMENT(4),
    CUESHEET(5),
    PICTURE(6);

    private final int id;

    BlockType(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
