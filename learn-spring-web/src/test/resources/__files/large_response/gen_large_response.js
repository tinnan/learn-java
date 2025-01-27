const fs = require('fs')

const items = []
for (let i = 0; i < 400000; i += 1) {
    const item = {
        field0: "Field data",
        field1: "Field data",
        field2: "Field data",
        field3: "Field data",
        field4: "Field data",
        field5: "Field data",
        field6: "Field data",
        field7: "Field data",
        field8: "Field data",
        field9: "Field data",
    }
    items.push(item)
}
const fileData = {
    items
}
fs.writeFileSync('large_response.json', JSON.stringify(fileData))
