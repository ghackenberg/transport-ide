// Math classes

class Vector {
    constructor(x, y) {
        // Check paramters
        if (typeof x != 'number')
            throw new Error('X is not a number')
        if (typeof y != 'number')
            throw new Error('Y is not a number')

        this.x = x
        this.y = y
    }

    // Vector-scalar operations

    addScalar(scalar) {
        // Check parameters
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x + scalar, this.y + scalar)
    }
    substractScalar(scalar) {
        // Check parameters
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x - scalar, this.y - scalar)
    }
    multiplyScalar(scalar) {
        // Check parameters
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x * scalar, this.y * scalar)
    }
    divideScalar(scalar) {
        // Check parameters
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x / scalar, this.y / scalar)
    }

    // Vector-vector operations

    addVector(other) {
        if (!(other instanceof Vector))
            throw new Error('Other is not a vector')

        return new Vector(this.x + other.x, this.y + other.y)
    }
    substractVector(other) {
        if (!(other instanceof Vector))
            throw new Error('Other is not a vector')

        return new Vector(this.x - other.x, this.y - other.y)
    }
    multiplyVector(other) {
        if (!(other instanceof Vector))
            throw new Error('Other is not a vector')

        return this.x * other.x + this.y * other.y
    }

    // Vector operations

    length() {
        return Math.sqrt(this.multiplyVectorDot(this))
    }
    normalize() {
        return this.divideScalar(this.length())
    }
}

class Line {
    constructor(source, target) {
        if (!(source instanceof Vector))
            throw new Error('Source is not a vector')
        if (!(target instanceof Vector))
            throw new Error('Target is not a vector')

        this.source = source
        this.target = target

        this.direction = this.target.substractVector(this.source)
    }

    point(scalar) {
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return this.source.addVector(this.direction.multiplyScalar(scalar))
    }

    intersectScalar(other) {
        if (!(other instanceof Line))
            throw new Error('Other is not a line')

        // See https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
        
        const x1 = this.source.x
        const y1 = this.source.y

        const x2 = this.target.x
        const y2 = this.target.y

        const x3 = other.source.x
        const y3 = other.source.y

        const x4 = other.target.x
        const y4 = other.target.y

        const a = (x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)
        const b = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)

        return a / b
    }
    intersectVector(other) {
        return this.point(this.intersectScalar(other))
    }
}

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