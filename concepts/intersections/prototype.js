// Model classes

class Intersection {
    constructor(key, x, y) {
        // Check parameters
        if (typeof x != 'number')
            throw new Error('X is not a number')
        if (typeof y != 'number')
            throw new Error('Y is not a number')

        this.key = key
        
        this.x = x
        this.y = y

        this.incoming = []
        this.outgoing = []
    }
}

class Segment {
    constructor(source, target, lanes) {
        // Check parameters
        if (!(source instanceof Intersection))
            throw new Error('Source is not an intersection')
        if (!(target instanceof Intersection))
            throw new Error('Target is not an intersection')
        if (typeof lanes != 'number')
            throw new Error('Lanes is not a number')

        this.source = source
        this.target = target

        this.source.outgoing.push(this)
        this.target.incoming.push(this)
        
        this.lanes = lanes
    }
}

class Infrastructure {
    constructor() {
        this.intersections = {}
        this.segments = []
    }
    addIntersection(key, x, y) {
        // Check key
        if (key in this.intersections)
            throw new Error('Intersection already exists')
        // Create, store, and return intersection
        return this.intersections[key] = new Intersection(key, x, y)
    }
    addSegment(source, target, lanes) {
        // Resolve source and target
        source = typeof source == 'string' ? this.intersections[source] : source
        target = typeof target == 'string' ? this.intersections[target] : target
        // Create, store, and return segment
        const segment = new Segment(source, target, lanes)
        this.segments.push(segment)
        return segment
    }
}

// Test cases

// Test case 0

const tc0 = new Infrastructure()
tc0.addIntersection('A', 0, 0)
tc0.addIntersection('B', 100, 100)
tc0.addSegment('A', 'B', 1)

// Test case 1

const tc1 = new Infrastructure()
tc1.addIntersection('A', 0, 0)
tc1.addIntersection('B', 100, 100)
tc1.addIntersection('C', 500, 220)
tc1.addIntersection('D', 100, 700)
tc1.addSegment('A', 'B', 1)
tc1.addSegment('A', 'C', 1)
tc1.addSegment('A', 'D', 1)

// Drawing functionality

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