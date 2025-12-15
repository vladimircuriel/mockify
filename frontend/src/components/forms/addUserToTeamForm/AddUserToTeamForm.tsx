import AddToUserToTeamModal from '@components/modal/addUserToTeamModal/AddUserToTeamModal'
import { Form, Input } from '@heroui/react'
import { addUserToTeam } from '@lib/actions/project.action'
import type { AuthPackage } from '@lib/entity/auth.entity'
import type { Project } from '@lib/entity/project.entity'
import { useTranslations } from 'next-intl'
import { startTransition, useActionState, useState } from 'react'

export type AddUserToTeamFormProps = {
  authPackage: AuthPackage
  project: Project
}

export default function AddUserToTeamForm({ project, authPackage }: AddUserToTeamFormProps) {
  const [state, action, pending] = useActionState(addUserToTeam, {
    errors: {
      username: '',
    },
  })

  const [username, setUsername] = useState('')

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const form = e.currentTarget

    startTransition(() => {
      if (form.checkValidity()) {
        const formData = new FormData(form)
        action(formData)
      } else {
        form.reportValidity()
      }
    })
  }

  const t = useTranslations('addUserToTeamForm')

  return (
    <Form
      id="add-user-to-team"
      className="flex flex-row items-center justify-between w-full p-4"
      validationBehavior="native"
      validationErrors={state?.errors}
      onSubmit={handleSubmit}
    >
      <input type="hidden" name="projectId" value={project.id} />
      <input type="hidden" name="jwt" value={authPackage.jwt} />
      <Input
        isRequired
        label={t('usernameLabel')}
        isClearable
        name="username"
        placeholder={t('usernamePlaceholder')}
        type="text"
        variant="bordered"
        size="lg"
        radius="full"
        onKeyDown={(e) => {
          if (e.key === 'Enter') {
            e.preventDefault()
          }
        }}
        onChange={(e) => setUsername(e.target.value)}
      />
      <div className="flex flow-row items-center gap-x-2 justify-end w-full">
        {state?.errors?.username && (
          <span className="text-red-500 text-sm">{state?.errors.username}</span>
        )}
        <AddToUserToTeamModal pending={pending} username={username} />
      </div>
    </Form>
  )
}
