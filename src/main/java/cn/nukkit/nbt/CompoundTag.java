package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CompoundTag extends Tag {
    private Map<String, Tag> tags = new HashMap<>();

    public CompoundTag() {
        super("");
    }

    public CompoundTag(String name) {
        super(name);
    }

    void write(DataOutput dos) throws IOException {
        for (Tag tag : tags.values()) {
            Tag.writeNamedTag(tag, dos);
        }
        dos.writeByte(Tag.TAG_End);
    }

    void load(DataInput dis) throws IOException {
        tags.clear();
        Tag tag;
        while ((tag = Tag.readNamedTag(dis)).getId() != Tag.TAG_End) {
            tags.put(tag.getName(), tag);
        }
    }

    public Collection<Tag> getAllTags() {
        return tags.values();
    }

    public byte getId() {
        return TAG_Compound;
    }

    public CompoundTag put(String name, Tag tag) {
        tags.put(name, tag.setName(name));
        return this;
    }

    public CompoundTag putByte(String name, byte value) {
        tags.put(name, new ByteTag(name, value));
        return this;
    }

    public CompoundTag putShort(String name, short value) {
        tags.put(name, new ShortTag(name, value));
        return this;
    }

    public CompoundTag putInt(String name, int value) {
        tags.put(name, new IntTag(name, value));
        return this;
    }

    public CompoundTag putLong(String name, long value) {
        tags.put(name, new LongTag(name, value));
        return this;
    }

    public CompoundTag putFloat(String name, float value) {
        tags.put(name, new FloatTag(name, value));
        return this;
    }

    public CompoundTag putDouble(String name, double value) {
        tags.put(name, new DoubleTag(name, value));
        return this;
    }

    public CompoundTag putString(String name, String value) {
        tags.put(name, new StringTag(name, value));
        return this;
    }

    public CompoundTag putByteArray(String name, byte[] value) {
        tags.put(name, new ByteArrayTag(name, value));
        return this;
    }

    public CompoundTag putIntArray(String name, int[] value) {
        tags.put(name, new IntArrayTag(name, value));
        return this;
    }

    public CompoundTag putList(ListTag<? extends Tag> listTag) {
        tags.put(listTag.getName(), listTag);
        return this;
    }

    public CompoundTag putCompound(String name, CompoundTag value) {
        tags.put(name, value.setName(name));
        return this;
    }

    public CompoundTag putBoolean(String string, boolean val) {
        putByte(string, val ? (byte) 1 : 0);
        return this;
    }

    public Tag get(String name) {
        return tags.get(name);
    }

    public boolean contains(String name) {
        return tags.containsKey(name);
    }

    public CompoundTag remove(String name) {
        tags.remove(name);
        return this;
    }

    public byte getByte(String name) {
        if (!tags.containsKey(name)) return (byte) 0;
        return ((ByteTag) tags.get(name)).data;
    }

    public short getShort(String name) {
        if (!tags.containsKey(name)) return (short) 0;
        return ((ShortTag) tags.get(name)).data;
    }

    public int getInt(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((IntTag) tags.get(name)).data;
    }

    public long getLong(String name) {
        if (!tags.containsKey(name)) return (long) 0;
        return ((LongTag) tags.get(name)).data;
    }

    public float getFloat(String name) {
        if (!tags.containsKey(name)) return (float) 0;
        return ((FloatTag) tags.get(name)).data;
    }

    public double getDouble(String name) {
        if (!tags.containsKey(name)) return (double) 0;
        return ((DoubleTag) tags.get(name)).data;
    }

    public String getString(String name) {
        if (!tags.containsKey(name)) return "";
        return ((StringTag) tags.get(name)).data;
    }

    public byte[] getByteArray(String name) {
        if (!tags.containsKey(name)) return new byte[0];
        return ((ByteArrayTag) tags.get(name)).data;
    }

    public int[] getIntArray(String name) {
        if (!tags.containsKey(name)) return new int[0];
        return ((IntArrayTag) tags.get(name)).data;
    }

    public CompoundTag getCompound(String name) {
        if (!tags.containsKey(name)) return new CompoundTag(name);
        return (CompoundTag) tags.get(name);
    }

    @SuppressWarnings("unchecked")
    public ListTag<? extends Tag> getList(String name) {
        if (!tags.containsKey(name)) return new ListTag<>(name);
        return (ListTag<? extends Tag>) tags.get(name);
    }

    public boolean getBoolean(String string) {
        return getByte(string) != 0;
    }

    public String toString() {
        return "" + tags.size() + " entries";
    }

    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);
        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix += "   ";
        for (Tag tag : tags.values()) {
            tag.print(prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    public CompoundTag copy() {
        CompoundTag tag = new CompoundTag(getName());
        for (String key : tags.keySet()) {
            tag.put(key, tags.get(key).copy());
        }
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            CompoundTag o = (CompoundTag) obj;
            return tags.entrySet().equals(o.tags.entrySet());
        }
        return false;
    }
}
