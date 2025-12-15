import BreadcrumbsBuilder from '@components/breadcrumbsBuilder/BreadcrumbsBuilder'
import EndpointForm from '@components/forms/endpointForm/EndpointForm'
import Header from '@components/header/Header'

import { getProject } from '@lib/data/fetch/projects.fetch'
import Routes from '@lib/data/routes.data'
import { getTranslations } from 'next-intl/server'
import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'
import { getAuthUser } from '@lib/utils/auth.utils'

export default async function CreateEndpointPage({ params }: { params: Promise<{ id: string }> }) {
  const id = (await params).id
  const jwt = (await cookies()).get('session')?.value
  const [authPackage, authError] = await getAuthUser(jwt as string)
  if (authError) {
    return
  }

  const [_, error] = await getProject({ token: jwt as string, id })
  if (error?.message === 'Project not found') {
    redirect(Routes.Projects)
  }

  const t = await getTranslations('createEndpointPage')
  const t2 = await getTranslations('projectPage')

  return (
    <main className="flex flex-col items-center w-full mt-6">
      <Header title={`${t('title')}: ${id}`} description="Add your new endpoint here.">
        <BreadcrumbsBuilder
          items={[
            { name: t2('title'), href: Routes.Projects },
            { name: id, href: `${Routes.Projects}/${id}` },
            { name: 'Endpoint', href: '#' },
          ]}
        />
      </Header>
      <div className="w-full p-4 bg-black/30 rounded-3xl max-w-7xl">
        <EndpointForm projectId={id} authPackage={authPackage} />
      </div>
    </main>
  )
}
