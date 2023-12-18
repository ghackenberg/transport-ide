const canvas = document.getElementById("canvas")
const context = canvas.getContext("2d")

let state = 0

function next() {
    state++
    draw()
}

function draw() {
    // Resize canvas
    canvas.width = canvas.offsetWidth
    canvas.height = canvas.offsetHeight

    // Clear canvas
    context.clearRect(0, 0, canvas.width, canvas.height)

    // Draw rectangle
    context.save()
    context.fillStyle = 'red'
    context.fillRect(10 + state, 10, 20, 20)
    context.restore()

    // Draw circle
    context.save()
    context.beginPath()
    context.arc(100, 100, 20, 0, 2 * Math.PI)
    context.fillStyle = 'blue'
    context.fill()
    context.restore()

    // Draw line
    context.save()
    context.beginPath()
    context.moveTo(50, 100)
    context.lineTo(200, 300)
    context.lineTo(400, 100)
    context.lineWidth = 5
    context.strokeStyle = 'black'
    context.stroke()
    context.restore()
}

window.addEventListener('load', draw)
window.addEventListener('resize', draw)
window.addEventListener('keypress', next)