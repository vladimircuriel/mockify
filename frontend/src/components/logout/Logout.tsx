'use client'

import { logout } from '@lib/actions/auth.action'
import { Spinner } from '@heroui/react'
import { useEffect } from 'react'

// type LogOutProps = Readonly<{
//   jwt: string
// }>

export default function Logout() {
  useEffect(() => {
    const performLogout = async () => {
      await logout()
    }

    performLogout()
  }, [])
  return (
    <div className="flex items-center justify-center h-screen">
      <Spinner color="primary" label="Logging Out" />
    </div>
  )
}
