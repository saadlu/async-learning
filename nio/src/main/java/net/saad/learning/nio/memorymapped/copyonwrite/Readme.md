## Demonstrate Copy-on-write Memory Mapped Files

When a file is mapped to memory as a `PRIVATE` mapping, when first write is done, the update is done
on "private" copy of the relevent "page" (and hence copy on write). The changes are not written 
back to the disk (and hence it's "private").

On the other hand, if mapped as `READ_WRITE`, any write will be reflected back to the disk.

This example intened to demonstrate that

1. Copy-On-Write is private and not written back to disk
2. Read-Write mapping does write back to disk
3. Any update change in the disk are refleced in the mapped memory (regardlress of the MapMode)

It also intended to demonstrate that only the "page" in question is "copied"-on-write. (but I have 
failed to demonstrate that on a Macbook pro - not sure if that has to do with the page size)  


