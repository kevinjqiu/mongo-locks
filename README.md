# Distributed Locks using Mongo

## Document

```
{
    lockId: "LOCK_ID",
    owner: "OWNER_ID",
    acquiredAt: "2020-01-01T00:00:00.000Z",
    renewedAt: "..."
}
```


## Operations

### `tryAcquire(String lockId)`

### `acquire(String lockId)`

```
e.g., lock("abc")
```

Inserts a record

```
{
    lockId: "abc",
    owner: "owner_1",
    acquiredAt: NOW(),
    renewedAt: NOW()
}
```

### `unlock`

```
unlock("abc")
```
if is owner, delete the record with `lockId=abc and owner=owner_1"`

