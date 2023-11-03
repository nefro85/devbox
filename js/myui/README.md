# My UI

### Misc
```bash
npm run build && docker build -t s7i/fido-web .
```

```bash
docker run -d -p 8443:8443 -p 5005:5005 --network dev-network --name fido --rm s7i/fido-web
```

