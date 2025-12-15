type Position = 'top' | 'bottom' | 'left' | 'right'

type TestimonialsProps = {
  positions: Position[]
  children: React.ReactNode
  justify?: 'start' | 'end' | 'center'
}

const getPosition = (positions: Position[]) => {
  let className = ''
  positions.forEach((position) => {
    switch (position) {
      case 'top':
        className += ' top-10 '
        break
      case 'bottom':
        className += ' bottom-10 '
        break
      case 'left':
        className += ' left-10 '
        break
      case 'right':
        className += ' right-10 '
        break
      default:
        break
    }
  })

  return className
}

export default function Testimonials({
  positions,
  children,
  justify = 'start',
}: TestimonialsProps) {
  const className = getPosition(positions)
  const justifyClass = `text-${justify}`

  return (
    <div className={`absolute ${className} hidden md:block`}>
      <p className={`max-w-xl text-white/60 ${justifyClass} `}>
        <span className="font-medium">“</span>
        {children}
        <span className="font-medium">”</span>
      </p>
    </div>
  )
}
