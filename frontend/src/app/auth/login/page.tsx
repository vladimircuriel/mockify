import LogInForm from '@components/forms/logInForm/LogInForm'
import Testimonials from '@components/testimonials/Testimonials'
import { Spacer } from '@heroui/react'
import { useTranslations } from 'next-intl'

export default function LogInPage() {
  const t = useTranslations('testimonialSection')

  return (
    <div className="flex h-[calc(100dvh-60px)] flex-col w-full items-center justify-center overflow-hidden rounded-small p-2 sm:p-4 lg:p-8">
      <Testimonials positions={['bottom', 'left']}>{t('testimonial')}</Testimonials>

      <div className="w-full max-w-lg p-10 bg-black/30 rounded-3xl lg:mb-40">
        <h2 className="text-2xl font-medium text-center">{t('h2Text')}</h2>
        <Spacer y={2} />
        <LogInForm />
      </div>
    </div>
  )
}
