import { NewDiscussionForm } from "./NewDiscussionForm";

export const NewDiscussionTab = () => {

    return (

        <div className="card">

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Start a New Discussion</h2>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">

                {/* 说明信息 */}
                <div className="flex flex-col gap-4">

                    <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                        <h3 className="text-lg font-semibold text-blue-900 mb-2">
                            How can we help you?
                        </h3>
                        <p className="text-blue-800">
                            If you feel our service needs improvement or have suggestions,
                            please don't hesitate to share your thoughts!
                        </p>
                    </div>

                    <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                        <h3 className="text-lg font-semibold text-green-900 mb-2">
                            📚 Can't find a book?
                        </h3>
                        <p className="text-green-800">
                            We strive to make our collection fit everyone's needs.
                            If you're having trouble finding something, let us know!
                        </p>
                    </div>

                    <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
                        <h3 className="text-lg font-semibold text-purple-900 mb-2">
                            🔒 Privacy
                        </h3>
                        <p className="text-purple-800">
                            Your message is private and will only be visible to you
                            and our administration team.
                        </p>
                    </div>

                </div>

                {/* 表单 */}
                <div>
                    <NewDiscussionForm />
                </div>

            </div>

        </div>

    )

}
