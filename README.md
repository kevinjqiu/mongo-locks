# Distributed Locks using Mongo

## Document

```
{
    _id: LOCK_ID,
    owner: "OWNER_ID",
    updated: "2020-01-01T00:00:00.000Z"
}
```


## Operations

### `lock`

```
lock("abc")
```

Inserts a record

```
{
    _id: "abc",
    owner: "owner_1",
    updated: NOW()
}
```

### `unlock`

```
unlock("abc")
```
if is owner, delete the record with `_id="abc"`

